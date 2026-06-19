package main.java.server.commands;

import main.java.common.*;
import main.java.server.CollectionManager;
import main.java.server.ConnectionState;
import main.java.server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class ClearCommand implements Command, RequireAuthorization {

    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear : очистить свои элементы коллекции";
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

        ConnectionState connectionState =
                (ConnectionState) key.attachment();

        if (connectionState == null || !connectionState.isLoggedIn()) {
            return new Response(
                    "Вы должны войти в систему!",
                    StatusCode.UNAUTHORIZED,
                    null
            );
        }

        UserSession session =
                connectionState.getUserSession();

        Long userId = session.getUserId();

        if (collectionManager.isEmpty()) {
            return new Response(
                    "Коллекция уже пуста",
                    StatusCode.OK,
                    null
            );
        }

        int removedCount =
                collectionManager.clearDatabaseAndMemory(userId);

        return new Response(
                "Удалено элементов: " + removedCount,
                StatusCode.OK,
                null
        );
    }
}