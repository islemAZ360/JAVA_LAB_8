package client.gui.model;

import java.time.LocalDateTime;
import java.util.List;

public final class SampleData {
    private SampleData() {}

    public static List<HumanBeingUiModel> humans() {
        return List.of(
                new HumanBeingUiModel(1, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(2, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.KNIFE, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(3, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(4, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.HAMMER, new CarUiModel("Toyota", false), "user3"),

                new HumanBeingUiModel(5, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(6, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.KNIFE, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(7, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(8, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.HAMMER, new CarUiModel("Toyota", false), "user3"),

                new HumanBeingUiModel(13, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(14, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.KNIFE, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(15, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(16, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.HAMMER, new CarUiModel("Toyota", false), "user3"),

                new HumanBeingUiModel(17, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(19, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.KNIFE, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(20, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(21, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.HAMMER, new CarUiModel("Toyota", false), "user3"),

                new HumanBeingUiModel(9, "Bob", new CoordinatesUiModel(2, 5), LocalDateTime.now(), true, false, 12.5, 10, WeaponTypeUi.HAMMER, new CarUiModel("BMW", true), "user1"),
                new HumanBeingUiModel(10, "Ann", new CoordinatesUiModel(-4, 3), LocalDateTime.now(), false, true, 6.0, 20, WeaponTypeUi.KNIFE, new CarUiModel("Audi", false), "user2"),
                new HumanBeingUiModel(11, "Tom", new CoordinatesUiModel(6, -2), LocalDateTime.now(), true, true, 18.2, 7, WeaponTypeUi.KNIFE, new CarUiModel("Lada", true), "user1"),
                new HumanBeingUiModel(12, "Kate", new CoordinatesUiModel(-1, -4), LocalDateTime.now(), false, false, 4.3, 30, WeaponTypeUi.HAMMER, new CarUiModel("Toyota", false), "user3")
        );
    }
}
