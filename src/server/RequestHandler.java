package server;

import common.Request;
import common.Response;
import common.Command;
import common.StatusCode;

public class RequestHandler {

    private final CommandManager commandManager;
    private final CommandSuggester commandSuggester;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.commandSuggester = new CommandSuggester(commandManager);
    }

    public Response handle(Request request) {
        if (request == null) {
            return new Response("Запрос не может быть null", StatusCode.BAD_REQUEST, null);
        }

        String commandName = request.getCommandName();

        if (commandName == null || commandName.isBlank()) {
            return new Response("Имя команды не указано", StatusCode.BAD_REQUEST, null);
        }

        if (commandName.equals("save")) {
            return new Response("Эта комманда не доступна для клиента", StatusCode.BAD_REQUEST, null);
        }

        Command command = commandManager.getCommand(commandName);

        if (command == null) {
            return new Response("Команда не найдена: " + commandName + "\n" + this.commandSuggester.correct(commandName), StatusCode.BAD_REQUEST, null);
        }

        try {
            return command.execute(request);
        } catch (Exception e) {
            return new Response("Ошибка при выполнении команды: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
