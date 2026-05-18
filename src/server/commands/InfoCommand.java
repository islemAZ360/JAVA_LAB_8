package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.CollectionManager;

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
