package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.utils.CommandFileManager;
import common.utils.CommandManager;
import common.utils.HumanBeingFileManager;
import server.CollectionManager;

public class ExecuteScriptCommand implements Command {
//    private final CommandManager commandManager;
    private final CollectionManager collectionManager;
    private final HumanBeingFileManager humanBeingFileManager;

    public ExecuteScriptCommand(
            CollectionManager collectionManager,
//            CommandManager commandManager,
            HumanBeingFileManager humanBeingFileManager
    ) {
        this.collectionManager = collectionManager;
//        this.commandManager = commandManager;
        this.humanBeingFileManager = humanBeingFileManager;
    }

    @Override
    public String getName() {
        return "run_script_file";
    }

    @Override
    public String getDescription() {
        return "run_script_file file_path: вывести справку по доступным командам";
    }

    @Override
    public Response execute(Request request) {
        return new Response(
                "Команда run_script_file выполняется на клиенте.",
                StatusCode.BAD_REQUEST,
                null
        );
    }

//    public void execute(String[] args) {
//        if (args.length == 2) {
//            try {
//                CommandFileManager commandFileManager = new CommandFileManager(args[1]);
//                commandFileManager.readFileAndRunScripts(this.collectionManager, this.commandManager, this.humanBeingFileManager);
//            } catch (Exception e) {
//                System.out.println("Error in ExecuteScriptCommand in execute: " + e);
//            }
//        } else {
//            System.out.println("To use this command, please enter: " + this.getDescription());
//        }
//
//    }
}
