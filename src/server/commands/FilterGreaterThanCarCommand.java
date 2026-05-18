package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.HumanBeing;
import server.CollectionManager;

import java.util.List;

public class FilterGreaterThanCarCommand implements Command {

    private final CollectionManager collectionManager;

    public FilterGreaterThanCarCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_greater_than_car";
    }

    @Override
    public String getDescription() {
        return "filter_greater_than_car [true/false] : вывести элементы, значение car.cool которых больше заданного";
    }

    @Override
    public Response execute(Request request) {
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response("Значение car.cool не указано", StatusCode.BAD_REQUEST, null);
        }

        try {
            boolean filterValue = Boolean.parseBoolean(request.getStringArgument());

            List<HumanBeing> result = collectionManager.stream()
                    .filter(human -> {
                        boolean carCool = human.getCar() != null && human.getCar().isCool();
                        return carCool == filterValue;
                    })
                    .toList();

            if (result.isEmpty()) {
                return new Response(
                        "Элементы с car.cool = " + filterValue + " не найдены",
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    collectionManager.show(result),
                    StatusCode.OK,
                    result
            );
        } catch (Exception e) {
            return new Response("Ошибка при filter_greater_than_car: " + e.getMessage(), StatusCode.SERVER_ERROR, null);
        }
    }
}
