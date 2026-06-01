package server.commands;

import common.*;
import server.ConnectionState;
import server.auth.Account;
import server.auth.AccountService;
import server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class LoginCommand implements Command, RequireAuthorization {
    private final AccountService accountService;

    public LoginCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "login : вход в систему";
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            client.auth.Account account = (client.auth.Account) request.getObjectArgument();
            String username = account.getUsername();
            String password = account.getPassword();

            if (accountService.login(username, password)) {
                return new Response(
                        "Вход в систему успешен, привет '" + username + "' вернулся!",
                        StatusCode.OK,
                        null
                );
            }

            return new Response("Не удалось входить в систему", StatusCode.SERVER_ERROR, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при входе в систему: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }

    @Override
    public Response execute(Request request, SelectionKey key) {
        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            client.auth.Account account = (client.auth.Account) request.getObjectArgument();
            String username = account.getUsername();
            String password = account.getPassword();

            if (accountService.login(username, password)) {
                Account sessionAccount = accountService.getSession(username, password);
                UserSession session = new UserSession(sessionAccount.getUserId(), sessionAccount.getUsername());
                ConnectionState connectionState = (ConnectionState) key.attachment();
                connectionState.setUserSession(session);

//                System.out.println(key.attachment().toString());

                return new Response(
                        "Вход в систему успешен, привет '" + username + "' вернулся!",
                        StatusCode.OK,
                        null
                );
            }

            return new Response("Не удалось входить в систему", StatusCode.UNAUTHORIZED, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при входе в систему: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
