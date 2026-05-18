package client;

import common.HumanBeingBuilder;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.*;
import common.utils.BooleanBuilder;
import common.utils.LongBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.util.Arrays;
import java.util.Set;

public class InputManager {

    private final Scanner scanner;
    private final RequestSender reqSender;
    private final Coordinates coordinatesChecker;
    private final HumanBeingBuilder humanBeingBuilder;
    private final BooleanBuilder booleanBuilder;
    private final LongBuilder longBuilder;

    private Set<String> globalRunningStack = new HashSet<>();

    public InputManager(Scanner scanner, RequestSender reqSender) {
        this.scanner = scanner;
        this.reqSender = reqSender;
        this.coordinatesChecker = new Coordinates();
        this.booleanBuilder = new BooleanBuilder(scanner);
        this.humanBeingBuilder = new HumanBeingBuilder(this.scanner, this.coordinatesChecker, this.booleanBuilder);
        this.longBuilder = new LongBuilder(this.scanner);
    }

    public HumanBeing readHumanbeing() {
        return this.humanBeingBuilder.readHumanBeing();
    }

    public Boolean read_filter_greater_than_car_value() {
        return this.booleanBuilder.readBoolean("Введите значение фильтрации:");
    }

    public Long read_id_value() {
        return this.longBuilder.readLongId();
    }

    public HumanBeing readHumanbeing(String[] args) {
        return this.humanBeingBuilder.readHumanBeing(args);
    }

    public Response handleCommand(String input) throws IOException {

        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0];
        String argument = parts.length > 1 ? parts[1] : null;

        Request req = null;

        switch (commandName) {

            case "add":
                req = new Request(commandName, null, this.readHumanbeing());
                break;

            case "filter_greater_than_car":
                req = new Request(
                        commandName,
                        this.read_filter_greater_than_car_value().toString(),
                        null);
                break;

            case "add_if_max":
            case "add_if_min": {
                Long id;

                if (argument != null && !argument.isBlank()) {
                    id = Long.parseLong(argument);
                } else {
                    id = this.read_id_value();
                }

                req = new Request(commandName, id.toString(), null);
                Response addIfResp = this.reqSender.sendRequest(req);

                if (addIfResp.getStatusCode() == StatusCode.CONTINUE) {
                    System.out.println(addIfResp.getMessage());

                    req = new Request(
                            commandName,
                            id.toString(),
                            this.readHumanbeing()
                    );
                } else {
                    return addIfResp;
                }
                break;
            }

            case "update":
                req = new Request(commandName, argument, null);

                Response updateResp = this.reqSender.sendRequest(req);

                if (updateResp.getStatusCode() == StatusCode.CONTINUE) {
                    System.out.println(updateResp.getMessage());

                    req = new Request(
                            commandName,
                            argument,
                            this.readHumanbeing()
                    );
                } else {
                    return updateResp;
                }
                break;

            case "clear":
                req = new Request(commandName, argument, null);

                Response clearResp = this.reqSender.sendRequest(req);

                if (clearResp.getStatusCode() == StatusCode.CONTINUE) {
                    System.out.println(clearResp.getMessage());

                    String answer = scanner.nextLine();

                    req = new Request(commandName, answer, null);
                } else {
                    return clearResp;
                }
                break;

            case "show":
            case "info":
            case "help":
                req = new Request(commandName, null, null);
                break;

            case "exit":
                System.out.println("Client stopped.");
                System.exit(0);
                return null;

            case "save":
                System.out.println("Команда save недоступна в клиенте.");
                return null;

            case "run_script_file": {
                if (argument == null || argument.isBlank()) {
                    return new Response("Укажите путь к файлу скрипта.", StatusCode.BAD_REQUEST, null);
                }

                try (Scanner fileScanner = new Scanner(new File(argument))) {
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine().trim();

                        if (line.isEmpty() || line.startsWith("#")) continue;

                        String[] scriptParts = line.split("\\s+");
                        String scriptCommand = scriptParts[0];

                        System.out.println(">>> Reading and executing line: " + line);

                        if (scriptCommand.equals("run_script_file")) {
                            if (globalRunningStack.contains(scriptParts[1])) {
                                return new Response(
                                        "⚠️ Recursion prevent (cross or directly): " + scriptParts[1],
                                        StatusCode.OK,
                                        null
                                );
                            }
                            globalRunningStack.add(scriptParts[1]);
                        }

                        if (scriptCommand.equals("add")) {
                            if (scriptParts.length < 11) {
                                return new Response(
                                        "Ошибка: add должен быть в формате: add name x y realHero hasToothpick impactSpeed soundtrack minutes weapon car",
                                        StatusCode.BAD_REQUEST,
                                        null
                                );
                            }

                            String[] humanArgs = Arrays.copyOfRange(scriptParts, 1, 11);

                            Request addReq = new Request(
                                    scriptCommand,
                                    null,
                                    this.readHumanbeing(humanArgs)
                            );

                            Response addResp = this.reqSender.sendRequest(addReq);
                            System.out.println(addResp.getMessage());

                            continue;
                        }

                        if (scriptCommand.equals("add_if_max") || scriptCommand.equals("add_if_min")) {
                            if (scriptParts.length < 12) {
                                return new Response(
                                        "Ошибка: " + scriptCommand + " должен быть в формате: " +
                                                scriptCommand + " id name x y realHero hasToothpick impactSpeed soundtrack minutes weapon car",
                                        StatusCode.BAD_REQUEST,
                                        null
                                );
                            }

                            String scriptId = scriptParts[1];
                            String[] humanArgs = Arrays.copyOfRange(scriptParts, 2, 12);

                            Request checkReq = new Request(scriptCommand, scriptId, null);
                            Response checkResp = this.reqSender.sendRequest(checkReq);
                            System.out.println(checkResp.getMessage());

                            if (checkResp.getStatusCode() == StatusCode.CONTINUE) {
                                Request finalReq = new Request(
                                        scriptCommand,
                                        scriptId,
                                        this.readHumanbeing(humanArgs)
                                );

                                Response finalResp = this.reqSender.sendRequest(finalReq);
                                System.out.println(finalResp.getMessage());
                            }

                            continue;
                        }

                        Response scriptResp = this.handleCommand(line);
                        if (scriptResp != null) {
                            System.out.println(scriptResp.getMessage());
                        }
                    }

                    System.out.println("Скрипт успешно выполнен.");

                } catch (Exception e) {
                    return new Response("Ошибка чтения скрипта: " + e.getMessage(), StatusCode.BAD_REQUEST, null);
                } finally {
                    globalRunningStack.remove(argument);
                }

                return new Response("Скрипт успешно выполнен.", StatusCode.OK, null);
            }

            case "remove_by_id":
            case "remove_greater":
            case "filter_contains_name":
            case "filter_less_than_minutes_of_waiting":
            default:
                req = new Request(commandName, argument, null);
                break;
        }

        return this.reqSender.sendRequest(req);
    }
}
