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
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response("ID не указан", StatusCode.BAD_REQUEST, null);
        }

        try {
            long id = Long.parseLong(request.getStringArgument());

            boolean removed = collectionManager.removeFromDatabaseAndMemory(id);

            if (removed) {
                return new Response("Элемент удален", StatusCode.OK, null);
            }

            return new Response("Элемент не найден", StatusCode.ID_INVALID, null);
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом", StatusCode.ID_INVALID, null);
        } catch (Exception e) {
            return new Response("Ошибка при удалении: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}