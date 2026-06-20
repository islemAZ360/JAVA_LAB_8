package main.java.client.gui.mockup;

import main.java.client.gui.components.alert.AlertVariant;
import main.java.client.gui.components.alert.UiAlert;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.dialog.UiDialog;
import main.java.client.gui.components.field.UiField;
import main.java.client.gui.components.select.UiSelect;
import main.java.client.gui.model.CarUiModel;
import main.java.client.gui.model.CoordinatesUiModel;
import main.java.client.gui.model.HumanBeingUiModel;
import main.java.client.gui.model.WeaponTypeUi;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
        carName.setText(initial.car().name());
        carCool.setSelected(initial.car().cool());
    }

    private void saveResult() {
        try {
            String humanName = required(name.getText(), "name");
            int coordinateX = parseX(required(x.getText(), "coordinates.x"));
            long coordinateY = parseY(required(y.getText(), "coordinates.y"));
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
                    new CarUiModel(parsedCarName, carCool.isSelected()),
                    ownerLogin
            );
            close();
        } catch (NumberFormatException ex) {
            // число не распарсилось — показываем понятное сообщение, диалог не закрываем
            showError("Некорректное число: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // вылезли за границы домена (например X=745)
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // проверяем X на допустимые границы, иначе кидаем IllegalArgumentException
    private int parseX(String raw) {
        int value = Integer.parseInt(raw);
        if (value <= main.java.common.models.Const.MINVALUEX || value >= main.java.common.models.Const.MAXVALUEX) {
            throw new IllegalArgumentException(
                    "X должен быть больше " + main.java.common.models.Const.MINVALUEX
                            + " и меньше " + main.java.common.models.Const.MAXVALUEX + ". Получено: " + value);
        }
        return value;
    }

    // проверяем Y на верхнюю границу
    private long parseY(String raw) {
        long value = Long.parseLong(raw);
        if (value > main.java.common.models.Const.MAXVALUEY) {
            throw new IllegalArgumentException(
                    "Y должен быть не больше " + main.java.common.models.Const.MAXVALUEY + ". Получено: " + value);
        }
        return value;
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле " + field + " обязательно");
        }
        return value.trim();
    }

    private void showError(String message) {
        // заново навешиваем ERROR-стиль, чтобы алерт всегда был заметным
        error.applyVariant(AlertVariant.ERROR);
        error.setText("Ошибка проверки", message == null || message.isBlank() ? "Некорректные данные" : message);
        error.setVisible(true);
        error.setManaged(true);

        // поднимаем алерт наверх контента, чтобы пользователь точно его увидел
        content().getChildren().remove(error);
        content().getChildren().add(0, error);

        // если окно слишком низкое — немного растягиваем, иначе сообщение обрежется
        Stage stage = stage();
        if (stage.getHeight() < 620) {
            stage.setHeight(620);
        }
        stage.sizeToScene();
    }

    private String normalizeOwner(String currentUser) {
        return currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
    }

    private void add(GridPane grid, int col, int row, String label, javafx.scene.Node control) {
        grid.add(new UiField(label, control), col, row);
    }
}
