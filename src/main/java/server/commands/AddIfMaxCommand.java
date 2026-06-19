package main.java.server.commands;

import main.java.common.*;
import main.java.common.models.HumanBeing;
import main.java.server.CollectionManager;
import main.java.server.ConnectionState;
import main.java.server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class AddIfMaxCommand implements Command, RequireAuthorization {

    private final CollectionManager collectionManager;

    public AddIfMaxCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "add_if_max id {element} : добавить элемент, если его ID больше максимального";
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
        try {
            ConnectionState connectionState =
                    (ConnectionState) key.attachment();

            if (connectionState == null || !connectionState.isLoggedIn()) {
                return new Response(
                        "Вы должны войти в систему!",
                        StatusCode.UNAUTHORIZED,
                        null
                );
            }

            UserSession session = connectionState.getUserSession();
            Long userId = session.getUserId();

            Long newId = Long.parseLong(request.getStringArgument());

            if (request.getObjectArgument() == null) {
                if (isValidId(newId)) {
                    return new Response(
                            "Нужно заполнение полей информации объекта",
                            StatusCode.CONTINUE,
                            null
                    );
                }

                return new Response(
                        "ID не превышает максимальный ID в коллекции",
                        StatusCode.ID_INVALID,
                        null
                );
            }

            HumanBeing newHuman =
                    (HumanBeing) request.getObjectArgument();

            newHuman.setId(newId);
            newHuman.setUserId(userId);

            synchronized (collectionManager) {
                if (collectionManager.isEmpty()) {
                    collectionManager.addToDatabaseAndMemory(newHuman);

                    return new Response(
                            "Коллекция пуста. Элемент добавлен. ID: "
                                    + newHuman.getId(),
                            StatusCode.OK,
                            null
                    );
                }

                Long maxId = collectionManager.getMaxId();

                if (newHuman.getId() > maxId) {
                    collectionManager.addToDatabaseAndMemory(newHuman);

                    return new Response(
                            "Элемент добавлен, так как его ID больше максимального. ID: "
                                    + newHuman.getId(),
                            StatusCode.OK,
                            null
                    );
                }

                return new Response(
                        "Элемент не добавлен: его ID не превышает максимальный ID в коллекции ("
                                + maxId + ")",
                        StatusCode.ID_INVALID,
                        null
                );
            }

        } catch (Exception e) {
            return new Response(
                    "Ошибка при add_if_max: " + e.getMessage(),
                    StatusCode.SERVER_ERROR,
                    null
            );
        }
    }

    public Boolean isValidId(Long id) {
        return collectionManager.isEmpty()
                || id > collectionManager.getMaxId();
    }
}