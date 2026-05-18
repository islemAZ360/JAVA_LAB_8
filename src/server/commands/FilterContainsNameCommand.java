package server.commands;

import common.Command;
import common.Request;
import common.Response;
import common.StatusCode;
import common.models.HumanBeing;
import server.CollectionManager;

import java.util.List;

public class FilterContainsNameCommand implements Command {

    private final CollectionManager collectionManager;

    public FilterContainsNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_contains_name";
    }

    @Override
    public String getDescription() {
        return "filter_contains_name name : вывести элементы, имя которых содержит подстроку";
    }

    @Override
    public Response execute(Request request) {
        if (request.getStringArgument() == null || request.getStringArgument().isBlank()) {
            return new Response("Подстрока имени не указана\nИспользование: filter_contains_name name\nПример: filter_contains_name John", StatusCode.REQUIRED_FIELD_MISSING, null);
        }

        String nameSubstring = request.getStringArgument();

        List<HumanBeing> result = collectionManager.stream()
                .filter(human -> human.getName().toLowerCase().contains(nameSubstring.toLowerCase()))
                .toList();

        if (result.isEmpty()) {
            return new Response(
                    "Элементы с именем, содержащим \"" + nameSubstring + "\", не найдены",
                    StatusCode.OK,
                    null
            );
        }

        return new Response(
                collectionManager.show(result),
                StatusCode.OK,
                result
        );
    }
}
