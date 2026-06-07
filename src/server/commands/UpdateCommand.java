package server.commands;

import common.*;
import common.models.HumanBeing;
import server.CollectionManager;
import server.ConnectionState;
import server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class UpdateCommand implements Command, RequireAuthorization {

    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить элемент по ID";
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
                    "ID не указан\nИспользование: update id",
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

            HumanBeing existing = collectionManager.getHumanById(id);

            if (existing == null) {
                return new Response(
                        "Элемент с ID " + id + " не найден",
                        StatusCode.ID_INVALID,
                        null
                );
            }

            if (!existing.getUserId().equals(userId)) {
                return new Response(
                        "У вас нет прав изменять этот элемент!",
                        StatusCode.FORBIDDEN,
                        null
                );
            }

            if (request.getObjectArgument() == null) {
                return new Response(
                        "ID найден. Введите новые данные объекта.",
                        StatusCode.CONTINUE,
                        null
                );
            }

            HumanBeing newHuman = (HumanBeing) request.getObjectArgument();
            newHuman.setId(id);
            newHuman.setUserId(userId);

            boolean updated = collectionManager.updateInDatabaseAndMemory(id, newHuman);

            if (updated) {
                return new Response(
                        "Элемент обновлен",
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    "Элемент с ID " + id + " не найден",
                    StatusCode.ID_INVALID,
                    null
            );

        } catch (NumberFormatException e) {
            return new Response(
                    "Ошибка: ID должен быть числом",
                    StatusCode.ID_INVALID,
                    null
            );
        } catch (ClassCastException e) {
            return new Response(
                    "Ошибка: передан объект неверного типа",
                    StatusCode.BAD_REQUEST,
                    null
            );
        } catch (Exception e) {
            return new Response(
                    "Ошибка при обновлении: " + e.getMessage(),
                    StatusCode.SERVER_ERROR,
                    null
            );
        }
    }
}