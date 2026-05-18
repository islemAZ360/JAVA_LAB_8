package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.CollectionManager;

/**
 * Команда show: выводит все элементы коллекции.
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show : вывести все элементы коллекции в строковом представлении";
    }

    @Override
    public Response execute(Request request) {
        String result = collectionManager.show();
        return new Response(result, StatusCode.OK, null);
    }
}
