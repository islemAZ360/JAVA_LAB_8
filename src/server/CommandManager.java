package server;

import common.utils.HumanBeingFileManager;
import server.commands.*;
import common.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager(CollectionManager collectionManager, HumanBeingFileManager humanBeingFileManager) {
        commands.put("help", new HelpCommand(this));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("add_if_max", new AddIfMaxCommand(collectionManager));
        commands.put("add_if_min", new AddIfMinCommand(collectionManager));
        commands.put("remove_greater", new RemoveGreaterCommand(collectionManager));
        commands.put("filter_contains_name", new FilterContainsNameCommand(collectionManager));
        commands.put("filter_greater_than_car", new FilterGreaterThanCarCommand(collectionManager));
        commands.put("filter_less_than_minutes_of_waiting", new FilterLessThanMinutesOfWaitingCommand(collectionManager));
        commands.put("save", new SaveCommand(collectionManager, humanBeingFileManager));
        commands.put("run_script_file", new ExecuteScriptCommand(collectionManager, humanBeingFileManager));
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}
