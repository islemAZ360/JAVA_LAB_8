package main.java.client.gui.model;

import main.java.client.gui.core.DisplayField;
import main.java.client.gui.core.Displayable;
import main.java.client.gui.core.FieldRole;

import java.time.LocalDateTime;
import java.util.List;

public record HumanBeingUiModel(
        long id,
        String name,
        CoordinatesUiModel coordinates,
        LocalDateTime creationDate,
        boolean realHero,
        boolean hasToothpick,
        double impactSpeed,
        long minutesOfWaiting,
        WeaponTypeUi weaponType,
        CarUiModel car,
        String ownerLogin
) implements Displayable {

    @Override
    public List<DisplayField> toDisplayFields() {
        return List.of(
                new DisplayField("id", String.valueOf(id), FieldRole.META),
                new DisplayField("name", name, FieldRole.TITLE),
                new DisplayField("coordinates", coordinates.x() + "; " + coordinates.y(), FieldRole.SUBTITLE),
                new DisplayField("impactSpeed", String.valueOf(impactSpeed), FieldRole.META),
                new DisplayField("minutesOfWaiting", String.valueOf(minutesOfWaiting), FieldRole.META),
                new DisplayField("weaponType", String.valueOf(weaponType), FieldRole.BADGE),
                new DisplayField("car", car.name() + " / cool=" + car.cool(), FieldRole.META),
                new DisplayField("owner", ownerLogin, FieldRole.SUCCESS)
        );
    }
}
