package main.java.server.commands;

import main.java.common.*;
import main.java.common.models.HumanBeing;
import main.java.server.CollectionManager;
import main.java.server.ConnectionState;
import main.java.server.auth.UserSession;

import java.nio.channels.SelectionKey;
import java.util.Objects;
import java.util.Optional;

public class AddCommand implements Command, RequireAuthorization {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add {element} : добавить новый элемент в коллекцию";
    }

    @Override
    public Response execute(Request request) {
        return executeAfterAuthorization(request, null, null);
    }

    @Override
    public Response execute(Request request, SelectionKey key) {
        ConnectionState connectionState = (ConnectionState) key.attachment();

        // Проверка авторизации (Guard Clause)
        if (connectionState == null || !connectionState.isLoggedIn()) {
            return new Response(
                    "Вы должны войти в систему для использования!",
                    StatusCode.UNAUTHORIZED,
                    null
            );
        }

        UserSession userSession = connectionState.getUserSession();
        Long userId = userSession.getUserId();
        String username = userSession.getUsername();

        return executeAfterAuthorization(request, userId, username);
    }

    private Response executeAfterAuthorization(Request request, Long userId, String username) {
        return Optional.ofNullable(request.getObjectArgument())
                .filter(HumanBeing.class::isInstance)
                .map(HumanBeing.class::cast)
                .filter(tempHuman -> Objects.nonNull(userId))
                .map(tempHuman -> processAddition(tempHuman, userId, username))
                .orElse(new Response("Ошибка: объект не передан или имеет неверный тип", StatusCode.BAD_REQUEST, null));
    }

    // 3. Вынесение бизнес-логики добавления в отдельный метод
    private Response processAddition(HumanBeing tempHuman, Long userId, String username) {
        try {
            HumanBeing newHuman = collectionManager.generateNewInstance(tempHuman);
            newHuman.setUserId(userId);
            // сохраняем имя владельца в памяти, чтобы сразу отобразить в UI
            newHuman.setOwnerLogin(username);
//            long nextHumanId = collectionManager.getNextIdInRepository();
//            newHuman.setId(nextHumanId);
            long nextHumanId = collectionManager.addToDatabaseAndMemory(newHuman);

            if (nextHumanId != 0) {
                return new Response("Элемент добавлен. ID: " + nextHumanId, StatusCode.OK, null);
            }
            return new Response("Не удалось добавить элемент", StatusCode.SERVER_ERROR, null);

        } catch (Exception e) {
            return new Response("Ошибка при добавлении: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
