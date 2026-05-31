package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.auth.AccountService;

public class LogoutCommand implements Command {
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
        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            client.auth.Account account = (client.auth.Account) request.getObjectArgument();
            String username = account.getUsername();

            if (accountService.logout(username)) {
                return new Response(
                        "Вы успешно вышли из системы. До свидания, '" + username + "'!",
                        StatusCode.OK,
                        null
                );
            }

            return new Response("Не удалось выйти из системы", StatusCode.SERVER_ERROR, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при выходе из системы: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
