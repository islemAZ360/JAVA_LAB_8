package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.auth.AccountService;
import server.auth.UserSession;

import java.nio.channels.SelectionKey;

public class LoginCommand implements Command {
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
                UserSession session = new UserSession(username);
                key.attach(session);

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
}
