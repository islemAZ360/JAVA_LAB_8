package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.CollectionManager;

public class RemoveByIdCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "remove_by_id id : удалить элемент по ID";
    }

    @Override
    public Response execute(Request request) {
        if (request.getStringArgument() == null) {
            return new Response("ID не указан", StatusCode.BAD_REQUEST, null);
        }

        try {
            long id = Long.parseLong(request.getStringArgument().toString());

            boolean removed = collectionManager.removeById(id);

            if (removed) {
                return new Response("Элемент удален", StatusCode.OK, null);
            } else {
                return new Response("Элемент не найден", StatusCode.ID_INVALID, null);
            }

        } catch (Exception e) {
            return new Response("Ошибка: ID должен быть числом", StatusCode.ID_INVALID, null);
        }
    }
}
