package server.commands;

import common.Command;
import common.CommandFlag;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.Const;
import server.CollectionManager;

public class ClearCommand implements Command {

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
        return "clear : очистить коллекцию пользователя";
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.isEmpty()) {
            return new Response("Коллекция уже пуста", StatusCode.OK, null);
        }

        String arg = request.getStringArgument();

        if (arg != null) {
            String flag = arg.trim().toLowerCase();

            if (flag.equals(CommandFlag.YES.getLongFlag()) ||
                    flag.equals(CommandFlag.YES.getShortFlag()) ||
                    flag.equals(CommandFlag.FORCE.getLongFlag()) ||
                    flag.equals(CommandFlag.FORCE.getShortFlag())) {

                int oldSize = collectionManager.size();
                int removedCount = collectionManager.clearDatabaseAndMemory(Const.DEFAULT_USER_ID);

                return new Response(
                        "Коллекция очищена. Удалено элементов: " + removedCount,
                        StatusCode.OK,
                        null
                );
            }

            if (flag.equals("no") || flag.equals("-n")) {
                return new Response("Операция отменена.", StatusCode.OK, null);
            }

            return new Response(
                    String.format(
                            "Неверный аргумент. Используйте флаги %s или %s",
                            CommandFlag.FORCE.getLongFlag(),
                            CommandFlag.YES.getLongFlag()
                    ),
                    StatusCode.BAD_REQUEST,
                    null
            );
        }

        return new Response(
                "В коллекции " + collectionManager.size() + " элементов.\n" +
                        "Вы уверены? (yes/no)",
                StatusCode.CONTINUE,
                null
        );
    }
}
