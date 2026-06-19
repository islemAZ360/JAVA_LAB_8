package main.java.server.commands;

import main.java.common.Command;
import main.java.common.Request;
import main.java.common.Response;
import main.java.common.StatusCode;
import main.java.common.models.HumanBeing;
import main.java.server.CollectionManager;

import java.util.List;

public class FilterLessThanMinutesOfWaitingCommand implements Command {

    private final CollectionManager collectionManager;

    public FilterLessThanMinutesOfWaitingCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_less_than_minutes_of_waiting";
    }

    @Override
    public String getDescription() {
        return "filter_less_than_minutes_of_waiting minutes : фильтр по minutesOfWaiting";
    }

    @Override
    public Response execute(Request request) {
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response("minutesOfWaiting не указан", StatusCode.REQUIRED_FIELD_MISSING, null);
        }

        try {
            int minutes = Integer.parseInt(request.getStringArgument());

            List<HumanBeing> result = collectionManager.stream()
                    .filter(human -> human.getMinutesOfWaiting() < minutes)
                    .toList();

            if (result.isEmpty()) {
                return new Response(
                        "Элементы с minutesOfWaiting < " + minutes + " не найдены",
                        StatusCode.OK,
                        null
                );
            }

            return new Response(
                    collectionManager.show(result),
                    StatusCode.OK,
                    result
            );
        } catch (NumberFormatException e) {
            return new Response("Ошибка: minutes должно быть целым числом", StatusCode.FORMAT_INVALID, null);
        }
    }
}
