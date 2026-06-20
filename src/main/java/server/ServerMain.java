package main.java.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import main.java.common.Request;
import main.java.common.Serializer;
import main.java.common.Response;
import main.java.common.StatusCode;
import main.java.common.models.Const;
import main.java.common.models.HumanBeing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.java.server.auth.AccountRepository;
import main.java.server.auth.AccountService;
import main.java.server.auth.PostgresAccountRepository;
import main.java.server.auth.SessionManager;
import main.java.server.db.CollectionRepository;
import main.java.server.db.PostgresCollectionRepository;

public class ServerMain {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    private static RequestHandler requestHandler;

    private static final ForkJoinPool readPool = new ForkJoinPool();
    private static final ExecutorService processPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ExecutorService writePool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final AtomicInteger currentConnections = new AtomicInteger(0);

    private static final int MAX_CONNECTIONS = 2;

    private static final boolean USE_CLOUD = true;

    public static final Connection getConnection() {
        String dbUrl;
        String dbUser;
        String dbPassword;

        if (USE_CLOUD) {
            // Supabase
            dbUrl = Const.CLOUD_DB_URL;
            dbUser = Const.CLOUD_DB_USER_ENV;
            dbPassword = Const.CLOUD_DB_PASSWORD_ENV;
        } else {
            // Local
            dbUrl = Const.DB_URL;
            dbUser = System.getenv(Const.DB_USER_ENV);
            dbPassword = System.getenv(Const.DB_PASSWORD_ENV);
        }

        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            return conn;
        } catch (SQLException e) {
            logger.error("Не удалось поключить к PostgreSQL");
            return null;
        }
    }

//    public static Connection getConnection() {
//        String dbUrl = Const.DB_URL;
//        String dbUser = System.getenv(Const.DB_USER_ENV);
//        String dbPassword = System.getenv(Const.DB_PASSWORD_ENV);
//
//        System.out.println("DB_URL = " + dbUrl);
//        System.out.println("DB_USER = " + dbUser);
//        System.out.println("DB_PASSWORD exists = " + (dbPassword != null));
//
//        if (dbUser == null || dbPassword == null) {
//            throw new RuntimeException("DB_USER или DB_PASSWORD еще не установлены");
//        }
//
//        try {
//            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
//        } catch (SQLException e) {
//            logger.error("Не удается подключиться PostgreSQL: {}", e.getMessage());
//            throw new RuntimeException("Ошибка соединения PostgreSQL", e);
//        }
//    }

    public static CollectionRepository<HumanBeing> getHumanbeingRepository(Connection conn) {
        return new PostgresCollectionRepository(conn);
    }

    public static AccountRepository getAccountRepository(Connection conn) {
        return new PostgresAccountRepository(conn);
    }

    public static void main(String[] args) {
        logger.info("Hello! Log4j2 run successfully.");

        Connection conn = getConnection();
        CollectionRepository<HumanBeing> repository = getHumanbeingRepository(conn);
        AccountRepository postgresAccountRepository = getAccountRepository(conn);

        SessionManager sessionManager = new SessionManager();
        AccountService accountService = new AccountService(postgresAccountRepository, sessionManager);

        CollectionManager collectionManager = new CollectionManager(repository);
        collectionManager.loadFromRepository();

        CommandManager commandManager = new CommandManager(collectionManager, accountService);
        requestHandler = new RequestHandler(commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Сервер завершает работу. Выполняется остановка пулов потоков...");
            readPool.shutdown();
            processPool.shutdown();
            writePool.shutdown();
            logger.info("Коллекция хранится в PostgreSQL, сохранение в файл не выполняется.");
        }));

        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(Const.host, Const.port));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Server NIO is running at {}: {}", Const.host, Const.port);

            while (true) {
                if (selector.select(1000) == 0) continue;

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        handleAccept(serverChannel, selector);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка сервера: {}", e.getMessage());
        }
    }

    private static void handleAccept(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();

        if (currentConnections.get() < MAX_CONNECTIONS) {
            int connectionCount = currentConnections.incrementAndGet();

            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ, new ConnectionState());

            logger.info("Sent greeting and accepted: {}", clientChannel.getRemoteAddress());
            logger.info("Новое подключение принято. Всего подключений: {}", connectionCount);

            sendResponse(clientChannel, new Response(
                    String.format("Новое подключение принято. Всего подключений: %s", connectionCount),
                    StatusCode.OK,
                    null
            ));
        } else {
            logger.info(
                    "Подключение отклонено: достигнут лимит (макс. {}). Всего подключений: {}",
                    MAX_CONNECTIONS,
                    currentConnections.get()
            );

            sendResponse(clientChannel, new Response(
                    String.format(
                            "Подключение отклонено: достигнут лимит (макс. %s). Всего подключений: %s",
                            MAX_CONNECTIONS,
                            currentConnections.get()
                    ),
                    StatusCode.SERVICE_UNAVAILABLE,
                    null
            ));

            clientChannel.close();
        }
    }

    private static void handleRead(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ConnectionState state = (ConnectionState) key.attachment();

        try {
            if (!state.hasReadSize) {
                int read = sc.read(state.headerBuffer);

                if (read == -1) {
                    key.cancel();
                    sc.close();
                    currentConnections.getAndDecrement();
                    logger.info("Client left.");
                    return;
                }

                if (!state.headerBuffer.hasRemaining()) {
                    state.headerBuffer.flip();
                    int size = state.headerBuffer.getInt();

                    logger.info("Received Object: {} bytes", size);

                    if (size > 0) {
                        state.dataBuffer = ByteBuffer.allocate(size);
                        state.hasReadSize = true;
                    } else {
                        state.headerBuffer.clear();
                    }
                }
            }

            if (state.hasReadSize && state.dataBuffer != null) {
                sc.read(state.dataBuffer);

                if (!state.dataBuffer.hasRemaining()) {
                    byte[] data = state.dataBuffer.array();

                    state.headerBuffer.clear();
                    state.dataBuffer = null;
                    state.hasReadSize = false;

                    CompletableFuture.supplyAsync(() -> {
                        try {
                            Object requestObj = Serializer.deserialize(data);
                            logger.info("Received Request: {}", requestObj);
                            return (Request) requestObj;
                        } catch (Exception e) {
                            logger.error("Ошибка десериализации: {}", e.getMessage());
                            return null;
                        }
                    }, readPool).thenApplyAsync(request -> {
                        if (request != null) {
                            return requestHandler.handle(request, key);
                        }
                        return null;
                    }, processPool).thenAcceptAsync(response -> {
                        if (response != null) {
                            sendResponse(sc, response);
                        }
                    }, writePool);
                }
            }
        } catch (Exception e) {
            logger.error("Reading data error: {}", e.getMessage());

            try {
                sc.close();
                currentConnections.getAndDecrement();
            } catch (IOException ignored) {
            }
        }
    }

    private static void sendResponse(SocketChannel channel, Response responseObj) {
        try {
            byte[] data = Serializer.serialize(responseObj);

            ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
            buffer.putInt(data.length);
            buffer.put(data);
            buffer.flip();

            // блокируем канал, чтобы потоки из writePool не перемешали байты ответа
            synchronized (channel) {
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
            }

            logger.info("Send response to Client ({} bytes)", data.length);
        } catch (IOException e) {
            logger.error("Response sending error: {}", e.getMessage());

            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }
}
