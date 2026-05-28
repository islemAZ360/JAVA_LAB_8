package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import common.Request;
import common.Serializer;
import common.Response;
import common.StatusCode;
import common.models.Const;
import common.models.HumanBeing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.db.CollectionRepository;
import server.db.PostgresCollectionRepository;

public class ServerMain {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);

    private static RequestHandler requestHandler;

    private static final AtomicInteger currentConnections = new AtomicInteger(0);

    private static final int MAX_CONNECTIONS = 2;

    public static void main(String[] args) {
        logger.info("Hello! Log4j2 run successfully.");

//        String dbUrl = System.getenv().getOrDefault("DB_URL", Const.DB_URL);
//        String dbUser = System.getenv(Const.DB_USER_ENV);
//        String dbPassword = System.getenv(Const.DB_PASSWORD_ENV);

        String dbUrl = Const.DB_URL;
        String dbUser = System.getenv(Const.DB_USER_ENV);
        String dbPassword = System.getenv(Const.DB_PASSWORD_ENV);


        CollectionRepository<HumanBeing> repository =
                new PostgresCollectionRepository(dbUrl, dbUser, dbPassword);

        CollectionManager collectionManager = new CollectionManager(repository);
        collectionManager.loadFromRepository();

        CommandManager commandManager = new CommandManager(collectionManager);
        requestHandler = new RequestHandler(commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                logger.info("Сервер завершает работу. Коллекция хранится в PostgreSQL, сохранение в файл не выполняется.")
        ));

        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(Const.host, Const.port));
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("\uD83D\uDE80 Server NIO is running at {}: {}", Const.host, Const.port);

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

            logger.info("\uD83E\uDD1D Sent greeting and accepted: {}", clientChannel.getRemoteAddress());
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
                    logger.info("👋 Client leaved.");
                    return;
                }

                if (!state.headerBuffer.hasRemaining()) {
                    state.headerBuffer.flip();
                    int size = state.headerBuffer.getInt();

                    logger.info("\uD83D\uDCE6 Received Object: {} bytes", size);

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
                    Object requestObj = Serializer.deserialize(data);

                    logger.info("\uD83D\uDCE5 Received Request: {}", requestObj);

                    state.headerBuffer.clear();
                    state.dataBuffer = null;
                    state.hasReadSize = false;

                    Response response = requestHandler.handle((Request) requestObj);
                    sendResponse(sc, response);
                }
            }
        } catch (Exception e) {
            logger.error("❌ Reading data error: {}", e.getMessage());

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

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            logger.info("📤 Send response to Client ({} bytes)", data.length);
        } catch (IOException e) {
            logger.error("❌ Response sending error: {}", e.getMessage());

            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }
}