package main.java.server.commands;

import main.java.common.*;
import main.java.server.ConnectionState;
import main.java.server.auth.AccountService;
import main.java.server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class LogoutCommand implements Command, RequireAuthorization {
    private final AccountService accountService;

    public LogoutCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getName() {
        return "logout";
    }

    @Override
    public String getDescription() {
        return "logout : выход из системы";
    }

    @Override
    public Response execute(Request request) {
        return new Response("Вход в систему пока не произошел!", StatusCode.BAD_REQUEST, null);
    }

    @Override
    public Response execute(Request request, SelectionKey key) {
        try {
            // Delegation
            if (key != null && key.attachment() != null) {
                ConnectionState connectionState = (ConnectionState) key.attachment();
                UserSession userSession = connectionState.getUserSession();

                if (userSession != null) {
                    String username = userSession.getUsername();
                    if (username != null && accountService.logout(username)) {
                        connectionState.setUserSession(null);
                        return new Response(
                                "Вы успешно вышли из системы. До свидания, '" + username + "'!",
                                StatusCode.OK,
                                null
                        );
                    }
                }
            }
            return execute(request);
//            return new Response("Не удалось выйти из системы", StatusCode.SERVER_ERROR, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при выходе из системы: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }

}
