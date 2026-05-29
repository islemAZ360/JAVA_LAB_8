package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.Const;
import common.models.HumanBeing;
import server.CollectionManager;

public class AddIfMaxCommand implements Command {

    private final CollectionManager collectionManager;

    public AddIfMaxCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "add_if_max id {element} : добавить элемент, если его ID больше максимального";
    }

    @Override
    public Response execute(Request request) {
        try {
            Long newId = Long.parseLong(request.getStringArgument());

            if (request.getObjectArgument() == null) {
                if (isValidId(newId)) {
                    return new Response("Нужно заполнение полей информации объекта", StatusCode.CONTINUE, null);
                }

                return new Response("ID не превышает максимальный ID в коллекции", StatusCode.ID_INVALID, null);
            }

            HumanBeing newHuman = (HumanBeing) request.getObjectArgument();
            newHuman.setId(newId);
            newHuman.setOwnerLogin(Const.DEFAULT_OWNER_LOGIN);

            if (collectionManager.isEmpty()) {
                collectionManager.addToDatabaseAndMemory(newHuman);
                return new Response(
                        "Коллекция пуста. Элемент добавлен. ID: " + newHuman.getId(),
                        StatusCode.OK,
                        null
                );
            }

            Long maxId = collectionManager.getMaxId();

            if (newHuman.getId() > maxId) {
                collectionManager.addToDatabaseAndMemory(newHuman);
                return new Response(
                        "Элемент добавлен, так как его ID больше максимального. ID: " + newHuman.getId(),
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    "Элемент не добавлен: его ID не превышает максимальный ID в коллекции (" + maxId + ")",
                    StatusCode.ID_INVALID,
                    null
            );
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при add_if_max: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }

    public Boolean isValidId(Long id) {
        return collectionManager.isEmpty() || id > collectionManager.getMaxId();
    }
}