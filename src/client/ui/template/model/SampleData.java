package client.ui.template.model;

import java.time.LocalDateTime;
import java.util.List;

public final class SampleData {
    private SampleData() {}

    public static List<HumanBeingUiModel> humans() {
        return List.of(
                new HumanBeingUiModel(1, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, MoodUi.CALM, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(2, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.BAT, MoodUi.GLOOM, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(3, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, MoodUi.RAGE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(4, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.SHOTGUN, MoodUi.SADNESS, new CarUiModel("Toyota", false), "user3")
        );
    }
}
