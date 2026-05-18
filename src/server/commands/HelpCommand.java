package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.CommandManager;

public class HelpCommand implements Command {
    /**
     * Команда help: выводит справку по доступным командам.
     */
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public Response execute(Request request) {
        StringBuilder sb = new StringBuilder();
        commandManager.getCommands().values().forEach(cmd ->
                sb.append(cmd.getDescription()).append("\n")
        );

        return new Response(
                sb.toString().trim(),
                StatusCode.OK,
                null
        );
    }
}
