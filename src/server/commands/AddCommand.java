package server.commands;

import common.*;
import common.models.HumanBeing;
import server.CollectionManager;
import client.InputManager;

import java.util.Arrays;

/**
 * Команда add {element}: добавляет новый элемент в коллекцию.
 * Элемент читается с помощью ElementReader.
 * Lưu ý: Command này chỉ thêm vào collection, không tự động save vào file.
 * User phải gọi save riêng để lưu.
 */
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
        return "add {element} : добавить новый элемент в коллекцию (не сохраняет в файл)";
    }

    @Override
    public Response execute(Request request) {

        if (request.getObjectArgument() == null) {
            return new Response("Объект не передан", StatusCode.BAD_REQUEST, null);
        }

        try {
            HumanBeing tempHuman = (HumanBeing) request.getObjectArgument();
//            System.out.println(tempHuman.getCar()); // null
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

            boolean added = collectionManager.add(newHuman);

            if (added) {
                return new Response(
                        "Элемент добавлен. ID: " + newHuman.getId(),
                        StatusCode.OK,
                        null
                );
            } else {
                return new Response("Не удалось добавить элемент", StatusCode.SERVER_ERROR, null);
            }

        } catch (Exception e) {
            return new Response("Ошибка при добавлении: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
