package client.ui.template.mockup;

import client.ui.template.components.alert.AlertVariant;
import client.ui.template.components.alert.UiAlert;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.components.dialog.UiDialog;
import client.ui.template.components.field.UiField;
import client.ui.template.components.select.UiSelect;
import client.ui.template.model.CarUiModel;
import client.ui.template.model.CoordinatesUiModel;
import client.ui.template.model.HumanBeingUiModel;
import client.ui.template.model.MoodUi;
import client.ui.template.model.WeaponTypeUi;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.util.Arrays;

public class HumanBeingFormDialog extends UiDialog {
    private final long resultId;
    private final String ownerLogin;
    private final HumanBeingUiModel initial;

    private HumanBeingUiModel result;
    private UiAlert error;

    private TextField name;
    private TextField x;
    private TextField y;
    private CheckBox realHero;
    private CheckBox hasToothpick;
    private TextField impactSpeed;
    private TextField minutes;
    private UiSelect<WeaponTypeUi> weaponType;
    private UiSelect<MoodUi> mood;
    private TextField carName;
    private CheckBox carCool;

    public HumanBeingFormDialog(Window owner, String title) {
        this(owner, title, null, "demo_user", System.currentTimeMillis() % 100_000);
    }

    public HumanBeingFormDialog(Window owner, String title, HumanBeingUiModel initial, String currentUser, long newId) {
        super(title, owner);
        this.initial = initial;
        this.resultId = initial == null ? newId : initial.id();
        this.ownerLogin = initial == null ? normalizeOwner(currentUser) : initial.ownerLogin();
        buildForm();
        fillInitialData();
    }

    public HumanBeingUiModel result() {
        return result;
    }

    private void buildForm() {
        error = new UiAlert("Ошибка", "", AlertVariant.ERROR);
        error.setVisible(false);
        error.setManaged(false);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("human-form-grid");
        grid.setHgap(12);
        grid.setVgap(12);

        name = new TextField();
        x = new TextField();
        y = new TextField();
        realHero = new CheckBox();
        hasToothpick = new CheckBox();
        impactSpeed = new TextField();
        minutes = new TextField();
        weaponType = new UiSelect<>(Arrays.asList(WeaponTypeUi.values())).selectFirstIfAny();
        mood = new UiSelect<>(Arrays.asList(MoodUi.values())).selectFirstIfAny();
        carName = new TextField();
        carCool = new CheckBox();

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

        content().getChildren().addAll(error, grid);

        UiButton save = new UiButton("Сохранить");
        UiButton cancel = new UiButton("Отмена", ButtonVariant.OUTLINE);
        cancel.setOnAction(e -> close());
        save.setOnAction(e -> saveResult());
        footer().getChildren().addAll(save, cancel);
    }

    private void fillInitialData() {
        if (initial == null) {
            name.setText("NewHuman" + resultId);
            x.setText("0");
            y.setText("0");
            impactSpeed.setText("10");
            minutes.setText("5");
            carName.setText("Car" + resultId);
            return;
        }

        name.setText(initial.name());
        x.setText(String.valueOf(initial.coordinates().x()));
        y.setText(String.valueOf(initial.coordinates().y()));
        realHero.setSelected(initial.realHero());
        hasToothpick.setSelected(initial.hasToothpick());
        impactSpeed.setText(String.valueOf(initial.impactSpeed()));
        minutes.setText(String.valueOf(initial.minutesOfWaiting()));
        weaponType.getSelectionModel().select(initial.weaponType());
        mood.getSelectionModel().select(initial.mood());
        carName.setText(initial.car().name());
        carCool.setSelected(initial.car().cool());
    }

    private void saveResult() {
        try {
            String humanName = required(name.getText(), "name");
            int coordinateX = Integer.parseInt(required(x.getText(), "coordinates.x"));
            long coordinateY = Long.parseLong(required(y.getText(), "coordinates.y"));
            double speed = Double.parseDouble(required(impactSpeed.getText(), "impactSpeed"));
            long waiting = Long.parseLong(required(minutes.getText(), "minutesOfWaiting"));
            String parsedCarName = required(carName.getText(), "car.name");

            result = new HumanBeingUiModel(
                    resultId,
                    humanName,
                    new CoordinatesUiModel(coordinateX, coordinateY),
                    initial == null ? LocalDateTime.now() : initial.creationDate(),
                    realHero.isSelected(),
                    hasToothpick.isSelected(),
                    speed,
                    waiting,
                    weaponType.selected(),
                    mood.selected(),
                    new CarUiModel(parsedCarName, carCool.isSelected()),
                    ownerLogin
            );
            close();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле " + field + " обязательно");
        }
        return value.trim();
    }

    private void showError(String message) {
        error.setText("Ошибка", message == null ? "Некорректные данные" : message);
        error.setVisible(true);
        error.setManaged(true);
    }

    private String normalizeOwner(String currentUser) {
        return currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
    }

    private void add(GridPane grid, int col, int row, String label, javafx.scene.Node control) {
        grid.add(new UiField(label, control), col, row);
    }
}
