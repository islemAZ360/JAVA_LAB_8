package main.java.server.commands;

import main.java.common.Command;
import main.java.common.Request;
import main.java.common.Response;
import main.java.common.StatusCode;
import main.java.server.CollectionManager;

/**
 * Команда info: выводит информацию о коллекции.
 */
public class InfoCommand implements Command {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "info : вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }

    @Override
    public Response execute(Request request) {
        return new Response(
                collectionManager.getCollectionInfo(),
                StatusCode.OK,
                null
        );
    }
}
