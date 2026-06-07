package server.commands;

import common.*;
import common.models.HumanBeing;
import server.CollectionManager;
import server.ConnectionState;
import server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class RemoveByIdCommand implements Command, RequireAuthorization {

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
                    "ID не указан",
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

            HumanBeing human = collectionManager.getHumanById(id);

            if (human == null) {
                return new Response(
                        "Элемент не найден",
                        StatusCode.ID_INVALID,
                        null
                );
            }

            if (!human.getUserId().equals(userId)) {
                return new Response(
                        "У вас нет прав удалить этот элемент!",
                        StatusCode.FORBIDDEN,
                        null
                );
            }

            boolean removed = collectionManager.removeFromDatabaseAndMemory(id);

            if (removed) {
                return new Response(
                        "Элемент удален",
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    "Элемент не найден",
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
                    "Ошибка при удалении: " + e.getMessage(),
                    StatusCode.SERVER_ERROR,
                    null
            );
        }
    }
}