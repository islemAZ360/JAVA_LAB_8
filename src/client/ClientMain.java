package client;

import common.Response;
import common.Serializer;
import common.StatusCode;
import common.models.Const;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClientMain {
    private static int current_timeout_index = 0;
    private static final int[] timeouts = {5, 15, 30, 60, 120, 300, 600};
    private static final int BASE_TIMEOUT = 5;

    private static Socket socket = null;
    private static volatile boolean isConnected = false;
    private static volatile boolean isReconnecting = false;

    private static DataOutputStream dos;
    private static DataInputStream dis = null;
    private static RequestSender reqSender = null;
    private static InputManager inputMng = null;
    private static Scanner scanner = new Scanner(System.in);

    private static ScheduledFuture<?> currentTask;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public static void main(String[] args) {
        // Try to connect when start
        isConnected = reconnect();

        // Check Socket connection
        scheduler.scheduleAtFixedRate(() -> {
            // If isReconnecting do not thing, to avoid duplicate task
            if (isReconnecting) return;

            if (!isSocketAlive(socket)) {
                System.out.println("\nDisconnected to server, start reconnecting ...");
                isConnected = false;
                isReconnecting = true; // prevent next task of scheduleAtFixedRate
                current_timeout_index = 0;
                triggerReconnect();
            }
        }, 0, BASE_TIMEOUT, TimeUnit.SECONDS);

        while (true) {
            if (isConnected && !isReconnecting) {
                System.out.println("\nПрограмма готова к работе! Введите 'help' для подержки || 'exit' для выхода.");
                System.out.println(">>> \uD83E\uDD16 Что вы думаете? Чем я могу помочь?");
                System.out.print(">>> ");
            }

            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Завершение работы программы...");
                break;
            }

            if ("reconnect".equalsIgnoreCase(input)) {
                // If manual typing command (reconnect), cancel automatic task in process/queue to avoid 2 tasks run at the same time
                if (currentTask != null) currentTask.cancel(true);

                isConnected = reconnect();

                if (isConnected) {
                    isReconnecting = false;
                    Terminal.stopAnimation(false);
                } else {
                    // Fail with manual command (reconnect), continues with automatic reconnecting
                    isReconnecting = true;
                    triggerReconnect();
                }
                continue;
            }

            if (!isConnected) {
                log("❌ Connecting error: Server is not response. (If server is not running, start server first!)");
            } else {
                try {
                    Response resp = inputMng.handleCommand(input);
                    System.out.println("📩 Server response:\n" + resp.getMessage());
                } catch (Exception e) {
                    isConnected = false;
                    log("❌ Connecting error: Server is not response.");
                }
            }
        }
    }

    public static boolean isSocketAlive(Socket socket) {
        try {
            if (socket == null || socket.isClosed()) return false;
            socket.sendUrgentData(0xFF);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean reconnect() {
        try {
            if (socket != null) socket.close(); // Closing old task before create new one

            socket = new Socket(Const.host, Const.port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            reqSender = new RequestSender(dos, dis);
            inputMng = new InputManager(scanner, reqSender);

            int size = dis.readInt();
            byte[] data = new byte[size];
            dis.readFully(data);
            Response firstRes = (Response) Serializer.deserialize(data);

            if (firstRes.getStatusCode() != StatusCode.SERVICE_UNAVAILABLE) {
                Terminal.stopAnimation();
                System.out.print(Const.GREEN + Const.cat + Const.RESET);
                log("✅ Connected to server successfully!");
                log(firstRes.getMessage());
                isConnected = true;
                isReconnecting = false;
                return true;
            } else {
                log(firstRes.getMessage());
                Terminal.startAnimation("Reconnecting to server...");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void triggerReconnect() {
        // Always cancel old task before creating new task to avoid duplication
        if (currentTask != null) currentTask.cancel(false);

        boolean success = reconnect();
        if (success) {
            isReconnecting = false;
            Terminal.stopAnimation();
            log("\nПрограмма готова к работе! Введите 'help' для подержки || 'exit' для выхода.");
            log(">>> \uD83E\uDD16 Что вы думаете? Чем я могу помочь?");
            System.out.print(">>> ");
        } else {
            int waitTime = timeouts[current_timeout_index];
            Terminal.log(String.format(
                    "⏳ ИСПОЛЗУЙТЕ каманду (reconnect) или подождите %d сек перед началом новой попытки подключения ...",
                    waitTime
            ));

            if (current_timeout_index < timeouts.length - 1) {
                current_timeout_index++;
            }

            Terminal.startAnimation("Reconnecting to server...");

            // Set schedule for next retries
            currentTask = scheduler.schedule(ClientMain::triggerReconnect, waitTime, TimeUnit.SECONDS);
        }
    }

    public static synchronized void log(String message) {
        Terminal.log(message);
    }
}


//============================================================= old version =============================================================
//package client;
//
//import common.Response;
//import common.Serializer;
//import common.StatusCode;
//import common.models.Const;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.Scanner;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public class ClientMain {
//    private static int current_timeout_index = 0;  // seconds
//    private static final int BASE_TIMEOUT = 5;
//    private static final int[] timeouts = {5, 15, 30, 60, 120, 300, 600};
//    private static Socket socket = null;
//    private static volatile boolean isConnected = false;
//    private static volatile boolean isReconnecting = false;
//    private static DataOutputStream dos;
//    private static DataInputStream dis = null;
//    private static RequestSender reqSender = null;
//    private static InputManager inputMng = null;
//    private static Scanner scanner = new Scanner(System.in);
//    private static String input;
//    private static ScheduledFuture<?> currentTask;
//    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
//        Thread t = new Thread(r);
//        t.setDaemon(true); // Để nó chạy ngầm
//        return t;
//    });
//
//    public static void main(String[] args) {
//        reconnect();
//
//        scheduler.scheduleAtFixedRate(() -> {
//            if (isReconnecting) return; // prevent call when cli is trying to reconnect to server
//            boolean isSocketAlive = isSocketAlive(socket);
//            if (!isSocketAlive) {
//                System.out.println("\nDisconnected to server, start reconnecting ...");
//                isConnected = reconnect();
//                if (!isConnected) {
//                    isReconnecting = true; // trigger prevent flag
//                    current_timeout_index = 0;
//                    triggerReconnect();
//                    try {
//                        Thread.sleep(timeouts[current_timeout_index] * 1000);
//                    } catch (InterruptedException e) {
//                        System.out.printf("❌ Some errors were occurred: %s.\n", e.getMessage());
//                    }
//                }
//            }
//        }, 0, BASE_TIMEOUT, TimeUnit.SECONDS);
//
//        while (true) {
//            if (isConnected && !isReconnecting) {
//                System.out.println("\nПрограмма готова к работе! Введите 'help' для подержки || 'exit' для выхода.");
//                System.out.println(">>> \uD83E\uDD16 Что вы думаете? Чем я могу помочь?");
//                System.out.print(">>> ");
//                input = scanner.nextLine();
//            } else {
//                input = scanner.nextLine();
//            }
//
//            if ("exit".equalsIgnoreCase(input)) {
//                System.out.println("Завершение работы программы...");
//                break;
//            } else if ("reconnect".equalsIgnoreCase(input)) {
//                isConnected = reconnect();
//                if (currentTask != null && isConnected) {
////                    ReconnectingEffectManager.stopEffect();
//                    Terminal.stopAnimation(false);
//                    currentTask.cancel(true);
//                    // false: does not cancel when executing.
//                    // true: cancel even when executing (throw InterruptedException where the thread/task is running | Thread.sleep()).
//                }
//            } else if (!isConnected) {
//                log("❌ Connecting error: Server is not response. (If server is not running, start server first!)");
//            } else {
//                try {
//                    Response resp = inputMng.handleCommand(input);
//                    System.out.println("📩 Server response:\n" + resp.getMessage());
//                } catch (IOException e) {
//                    log("❌ Connecting error: Server is not response. (If server is not running, start server first!)");
//                }
//            }
//        }
//    }
//
//    public static boolean isSocketAlive(Socket socket) {
//        try {
//            if (socket == null) {
//                return false;
//            }
//            // Gửi 1 byte dữ liệu khẩn cấp (0xFF) lên Server
//            // Nếu Server đã đóng hoặc crash, lệnh này sẽ ném ra IOException ngay lập tức
//            socket.sendUrgentData(0xFF);
//            return true;
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    public static boolean reconnect() {
//        try {
//            socket = new Socket(Const.host, Const.port);
//            dos = new DataOutputStream(socket.getOutputStream());
//            dis = new DataInputStream(socket.getInputStream());
//            reqSender = new RequestSender(dos, dis);
//            inputMng = new InputManager(scanner, reqSender);
//            isConnected = true;
//            isReconnecting = false;
//            int size = dis.readInt();
//            byte[] data = new byte[size];
//            dis.readFully(data);
//            Response firstRes = (Response) Serializer.deserialize(data);
//
//            if (firstRes.getStatusCode() != StatusCode.SERVICE_UNAVAILABLE) {
//                Terminal.stopAnimation();
//                System.out.printf(Const.GREEN + Const.cat + Const.RESET);
//                log("✅ Connected to server successfully!");
//                log(firstRes.getMessage());
//                return true;
//            } else {
//                log(firstRes.getMessage());
//                Terminal.startAnimation("Reconnecting to server...");
//                return false;
//            }
//        } catch (IOException e) {
//            log("❌ Failed to reconnect to the server!");
//            return false;
//        } catch (Exception e) {
//            log(String.format("❌ Some errors were occurred: %s.\n", e.getMessage()));
//            return false;
//        }
//    }
//
////    private static void triggerReconnect() {
////        boolean success = reconnect();
////
////        if (success) {
////            isReconnecting = false;
////            ReconnectingEffectManager.stopEffect();
////        } else {
////            int waitTime = timeouts[current_timeout_index];
////            log(String.format("⏳ ИСПОЛЗУЙТЕ каманду (reconnect) или подождите %d сек перед началом новой попытки подключения ...", waitTime));
////
////            if (current_timeout_index < timeouts.length - 1) {
////                current_timeout_index++;
////            }
////            System.out.println();
////            System.out.print(">>> "); // Dòng này dành cho bạn gõ
////            System.out.flush();
////
////            ReconnectingEffectManager.stopEffect();
////            ReconnectingEffectManager.startEffect("Reconnecting to server...");
////            // Next retry recursion (not Thread.sleep)
////            currentTask = scheduler.schedule(ClientMain::triggerReconnect, waitTime, TimeUnit.SECONDS);
////        }
////    }
//
//    private static void triggerReconnect() {
//        boolean success = reconnect();
//        if (success) {
//            isReconnecting = false;
//            Terminal.stopAnimation();
//        } else {
//            int waitTime = timeouts[current_timeout_index];
//            Terminal.log(String.format(
//                    "⏳ ИСПОЛЗУЙТЕ каманду (reconnect) или подождите %d сек перед началом новой попытки подключения ...",
//                    waitTime
//            ));
//
//            if (current_timeout_index < timeouts.length - 1) {
//                current_timeout_index++;
//            }
//
//            // ❌ XÓA 3 DÒNG NÀY — startAnimation() tự xử lý:
//            // System.out.println();
//            // System.out.print(">>> ");
//            // System.out.flush();
//
//            Terminal.startAnimation("Reconnecting to server...");
//            currentTask = scheduler.schedule(ClientMain::triggerReconnect, waitTime, TimeUnit.SECONDS);
//        }
//    }
//
//
//    public static synchronized void log(String message) {
//////        String currentPrompt = "\nПрограмма готова к работе! Введите 'help' для подержки || 'exit' для выхода.\n>>> \uD83E\uDD16 Что вы думаете? Чем я могу помочь?\n>>> ";
//////        System.out.print("\r" + "\u001B[1A" + "\u001B[2K" + "\u001B[1A" + "\u001B[2K" + "\u001B[1A" + "\u001B[2K");
//////        System.out.print("\r" + MOVE_UP + CLEAR_LINE + MOVE_UP + CLEAR_LINE + MOVE_UP + CLEAR_LINE); //ANSI code
////
////        String currentPrompt = ">>> ";
//////         1. \r: back to head of line
//////         2. space char: remove old line
//////         3. print log and newline
////        System.out.print("\r" + " ".repeat(currentPrompt.length() + 20) + "\r");
////
////        System.out.println(message);
////
////        // 4. reprint
////        System.out.print(currentPrompt);
////        System.out.flush();
//        Terminal.log(message);
//    }
//}
//
////            boolean isSocketAlive = isSocketAlive(socket);
////
////            if (!isSocketAlive) {
////                Thread thread = new Thread(ClientMain::reconnect);
////                thread.start();
////            } else {
////                try {
////                    Response resp = inputMng.handleCommand(input);
////                    System.out.println("📩 Server response:\n" + resp.getMessage());
////                } catch (IOException e) {
////                    System.err.println("❌ Connecting error: Server is not response. (If server is not running, start server first!)");
////                }
////            }