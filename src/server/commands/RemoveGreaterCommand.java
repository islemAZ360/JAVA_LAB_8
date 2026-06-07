package server.commands;

import common.*;
import common.models.HumanBeing;
import server.CollectionManager;
import server.ConnectionState;
import server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class RemoveGreaterCommand implements Command, RequireAuthorization {

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
        return "remove_greater id : удалить свои элементы с ID больше указанного";
    }

    @Override
    public Response execute(Request request) {
        return new Response(
                "Требуется авторизация",
                StatusCode.UNAUTHORIZED,
                null
        );
    }

    @Override
    public Response execute(Request request, SelectionKey key) {
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response(
                    "ID не указан\nИспользование: remove_greater id\nПример: remove_greater 10",
                    StatusCode.BAD_REQUEST,
                    null
            );
        }

        try {
            ConnectionState connectionState = (ConnectionState) key.attachment();

            if (connectionState == null || !connectionState.isLoggedIn()) {
                return new Response(
                        "Вы должны войти в систему!",
                        StatusCode.UNAUTHORIZED,
                        null
                );
            }

            UserSession session = connectionState.getUserSession();
            Long userId = session.getUserId();

            long id = Long.parseLong(request.getStringArgument());

            HumanBeing element = collectionManager.getHumanById(id);

            if (element == null) {
                return new Response(
                        "Элемент с ID " + id + " не найден",
                        StatusCode.ID_INVALID,
                        null
                );
            }

            int removedCount = collectionManager.removeGreaterFromDatabaseAndMemory(element, userId);

            if (removedCount > 0) {
                return new Response(
                        "Удалено ваших элементов с ID больше " + id + ": " + removedCount,
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    "Нет ваших элементов с ID больше " + id,
                    StatusCode.ID_INVALID,
                    null
            );

        } catch (NumberFormatException e) {
            return new Response(
                    "Ошибка: ID должен быть числом",
                    StatusCode.ID_INVALID,
                    null
            );
        } catch (Exception e) {
            return new Response(
                    "Ошибка при remove_greater: " + e.getMessage(),
                    StatusCode.SERVER_ERROR,
                    null
            );
        }
    }
}