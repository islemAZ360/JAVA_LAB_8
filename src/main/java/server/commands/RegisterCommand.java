package main.java.server.commands;

import main.java.common.Command;
import main.java.common.Request;
import main.java.common.Response;
import main.java.common.StatusCode;
import main.java.server.auth.AccountService;

public class RegisterCommand implements Command {
    private final AccountService accountService;

    public RegisterCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "register : регистрация в систему";
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            main.java.client.auth.Account account = (main.java.client.auth.Account) request.getObjectArgument();
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
