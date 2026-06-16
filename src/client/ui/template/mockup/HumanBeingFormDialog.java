package client.ui.template.mockup;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.components.dialog.UiDialog;
import client.ui.template.components.field.UiField;
import client.ui.template.components.select.UiSelect;
import client.ui.template.model.MoodUi;
import client.ui.template.model.WeaponTypeUi;

import java.util.Arrays;

public class HumanBeingFormDialog extends UiDialog {
    public HumanBeingFormDialog(Window owner, String title) {
        super(title, owner);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("human-form-grid");
        grid.setHgap(12);
        grid.setVgap(12);

        TextField name = new TextField();
        TextField x = new TextField();
        TextField y = new TextField();
        CheckBox realHero = new CheckBox();
        CheckBox hasToothpick = new CheckBox();
        TextField impactSpeed = new TextField();
        TextField minutes = new TextField();
        UiSelect<WeaponTypeUi> weaponType = new UiSelect<>(Arrays.asList(WeaponTypeUi.values())).selectFirstIfAny();
        UiSelect<MoodUi> mood = new UiSelect<>(Arrays.asList(MoodUi.values())).selectFirstIfAny();
        TextField carName = new TextField();
        CheckBox carCool = new CheckBox();

        add(grid, 0, 0, "name", name);
        add(grid, 1, 0, "coordinates.x", x);
        add(grid, 0, 1, "coordinates.y", y);
        add(grid, 1, 1, "realHero", realHero);
        add(grid, 0, 2, "hasToothpick", hasToothpick);
        add(grid, 1, 2, "impactSpeed", impactSpeed);
        add(grid, 0, 3, "minutesOfWaiting", minutes);
        add(grid, 1, 3, "weaponType", weaponType);
        add(grid, 0, 4, "mood", mood);
        add(grid, 1, 4, "car.name", carName);
        add(grid, 0, 5, "car.cool", carCool);

        content().getChildren().add(grid);

        UiButton save = new UiButton("Сохранить");
        UiButton cancel = new UiButton("Отмена", ButtonVariant.OUTLINE);
        cancel.setOnAction(e -> close());
        save.setOnAction(e -> close());
        footer().getChildren().addAll(save, cancel);
    }

    private void add(GridPane grid, int col, int row, String label, javafx.scene.Node control) {
        grid.add(new UiField(label, control), col, row);
    }
}
