package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.HumanBeing;
import server.CollectionManager;

public class RemoveGreaterCommand implements Command {

    private final CollectionManager collectionManager;

    public RemoveGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "remove_greater id : удалить элементы с ID больше указанного.";
    }

    @Override
    public Response execute(Request request) {
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response("ID не указан\nИспользование: remove_greater id\nПример: remove_greater 10.", StatusCode.BAD_REQUEST, null);
        }

        try {
            long id = Long.parseLong(request.getStringArgument());
            HumanBeing element = collectionManager.getHumanById(id);

            if (element == null) {
                return new Response("Элемент с ID " + id + " не найден", StatusCode.ID_INVALID, null);
            }

            int oldSize = collectionManager.size();
            collectionManager.removeGreater(element);
            int removedCount = oldSize - collectionManager.size();

            if (removedCount > 0) {
                return new Response(
                        "Удалено элементов с ID больше " + id + ": " + removedCount,
                        StatusCode.OK,
                        null
                );
            } else {
                return new Response("Нет элементов с ID больше " + id, StatusCode.ID_INVALID, null);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ID должен быть числом", StatusCode.ID_INVALID, null);
        } catch (Exception e) {
            return new Response("Ошибка при remove_greater: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
