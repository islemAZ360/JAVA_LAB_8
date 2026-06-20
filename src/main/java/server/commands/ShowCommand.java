package main.java.server.commands;

import main.java.common.Command;
import main.java.common.Request;
import main.java.common.Response;
import main.java.common.StatusCode;
import main.java.server.CollectionManager;

/**
 * Команда show: выводит все элементы коллекции.
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show : вывести все элементы коллекции в строковом представлении";
    }

    @Override
    public Response execute(Request request) {
        String result = collectionManager.show();
        // передаём саму коллекцию как data, иначе клиент получит null и таблица будет пустой
        return new Response(result, StatusCode.OK, new java.util.ArrayList<>(collectionManager));
    }
}
