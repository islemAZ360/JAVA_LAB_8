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

public class ClientMain1 {
    public static void main(String[] args) {
        boolean connected = false;
        int maxRetries = 5;
        int timeout = 5000;

        for (int i = 1; i <= maxRetries; i++) {
            // Use try-with-resources to auto close socket when error
            // Init output first to avoid Deadlock (server and client will be waiting header for each other => all hang)
            try (Socket socket = new Socket(Const.host, Const.port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 // Only this one communicates with Socket (read header and data => ois)
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                int size = dis.readInt();
                byte[] data = new byte[size];
                dis.readFully(data);
                Response firstRes = (Response) Serializer.deserialize(data);

                if (firstRes.getStatusCode() == StatusCode.SERVICE_UNAVAILABLE) {
                    System.out.println(firstRes.getMessage());
                    if (i < maxRetries) {
                        System.out.printf("Ожидание %d сек, перед началом новой попытки ...\n", timeout);
                        Thread.sleep(timeout);
                        continue; // redundant in this case
                    } else {
                        System.out.println("🚨 Количество попыток превышал количество максимальных попыток.");
                        break;
                    }
                } else {
                    RequestSender reqSender = new RequestSender(dos, dis);
                    Scanner scanner = new Scanner(System.in);
                    InputManager inputMng = new InputManager(scanner, reqSender);

                    System.out.println("✅ Connected to server successfully!");
                    System.out.println(firstRes.getMessage());
                    System.out.printf(Const.GREEN + Const.cat + Const.RESET);

                    // 2. Send Header instantly to server to get handshake response
                    // Translate byte[] to obj in deserialize
                    // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    // oos.flush();

                    while (true) {
                        System.out.println("\nПрограмма готова к работе! Введите 'help' для подержки || 'exit' для выхода.");
                        System.out.println(">>> \uD83E\uDD16 Что вы думаете? Чем я могу помочь?");
                        System.out.print(">>> ");
                        String input = scanner.nextLine();

                        if ("exit".equalsIgnoreCase(input)) break;

                        // Handles one or multiple spaces perfectly
//                String[] arguments = input.split("\\s+");

//  ===========================================================================================
                        Response resp = inputMng.handleCommand(input);

//                // 1. Create Request object
//                Request req = new Request(
//                        arguments[0],
//                        arguments.length>1? arguments[1]:null,
//                        "hello");
//
//                Response resp = reqSender.sendRequest(req);
                        System.out.println("📩 Server response:\n" + resp.getMessage());
//  ===========================================================================================
                    }
                    System.out.println("Завершение работы программы...");
                    break;
                }
            } catch (IOException e) {
//            System.out.println(e.getMessage());
                System.err.println("❌ Connecting error: Server is not response. (If server is not running, start server first!)");
                break;
            } catch (Exception e) {
                System.out.printf("❌ Some errors were occurred: %s.\n", e.getMessage());
            }
        }
    }
}
