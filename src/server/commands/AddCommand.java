package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.Const;
import common.models.HumanBeing;
import server.CollectionManager;

public class AddCommand implements Command {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add {element} : добавить новый элемент в коллекцию";
    }

    @Override
    public Response execute(Request request) {
        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            HumanBeing tempHuman = (HumanBeing) request.getObjectArgument();

            HumanBeing newHuman = new HumanBeing(
                    tempHuman.getName(),
                    tempHuman.getCoordinates(),
                    tempHuman.isRealHero(),
                    tempHuman.isHasToothpick(),
                    tempHuman.getImpactSpeed(),
                    tempHuman.getSoundtrackName(),
                    tempHuman.getMinutesOfWaiting(),
                    tempHuman.getWeaponType(),
                    tempHuman.getCar()
            );

            newHuman.setId(collectionManager.generateNextId());
            newHuman.setUserId(Const.DEFAULT_USER_ID);

            boolean added = collectionManager.addToDatabaseAndMemory(newHuman);

            if (added) {
                return new Response(
                        "Элемент добавлен. ID: " + newHuman.getId(),
                        StatusCode.OK,
                        null
                );
            }

            return new Response("Не удалось добавить элемент", StatusCode.SERVER_ERROR, null);
        } catch (ClassCastException e) {
            return new Response("Ошибка: передан объект неверного типа", StatusCode.BAD_REQUEST, null);
        } catch (Exception e) {
            return new Response("Ошибка при добавлении: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
