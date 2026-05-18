package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import server.CollectionManager;
import common.utils.HumanBeingFileManager;

public class SaveCommand implements Command {

    private final CollectionManager collectionManager;
    private final HumanBeingFileManager humanBeingFileManager;

    public SaveCommand(CollectionManager collectionManager, HumanBeingFileManager humanBeingFileManager) {
        this.collectionManager = collectionManager;
        this.humanBeingFileManager = humanBeingFileManager;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "save : сохранить коллекцию в файл";
    }

    @Override
    public Response execute(Request request) {
        try {
            humanBeingFileManager.saveAll(collectionManager);

            return new Response(
                    "Коллекция успешно сохранена в файл. Всего элементов: " + collectionManager.size(),
                    StatusCode.OK,
                    null
            );
        } catch (Exception e) {
            return new Response("Ошибка при сохранении: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
