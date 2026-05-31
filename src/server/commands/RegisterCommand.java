package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.auth.AccountService;

public class RegisterCommand implements Command {
    private final AccountService accountService;

    public RegisterCommand(AccountService accountService) {
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
            String email = account.getEmail();

            if (accountService.registerNewAccount(username, password, email)) {
                return new Response(
                        "Регистрация в систему произошла успешно, добро пожаловать '" + username + "' в систему!",
                        StatusCode.OK,
                        null
                );
            }

            return new Response("Не удалось регистрироваться в систему", StatusCode.SERVER_ERROR, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при регистрации в систему: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
