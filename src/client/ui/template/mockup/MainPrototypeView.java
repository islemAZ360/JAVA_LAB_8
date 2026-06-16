package client.ui.template.mockup;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import client.ui.template.components.alert.AlertVariant;
import client.ui.template.components.alert.UiAlert;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.components.card.UiCard;
import client.ui.template.components.datatable.ColumnSpec;
import client.ui.template.components.datatable.UiDataTable;
import client.ui.template.components.empty.UiEmpty;
import client.ui.template.components.resizable.ResizableSplitPane;
import client.ui.template.components.sidebar.UiSidebar;
import client.ui.template.core.Messages;
import client.ui.template.model.HumanBeingUiModel;
import client.ui.template.integration.Lab7CommandGateway;
import client.ui.template.integration.MockLab7CommandGateway;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainPrototypeView extends BorderPane {
    private final String currentUser;
    private final UiDataTable<HumanBeingUiModel> table = new UiDataTable<>();
    private final ObjectVisualizationCanvas canvas = new ObjectVisualizationCanvas();
    private final Label objectInfo = new Label("Выберите объект в таблице или на Canvas");
    private final Lab7CommandGateway gateway;
    private List<HumanBeingUiModel> items;

    public MainPrototypeView(String currentUser) {
        this(currentUser, new MockLab7CommandGateway());
    }

    public MainPrototypeView(String currentUser, Lab7CommandGateway gateway) {
        this.currentUser = currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
        this.gateway = gateway == null ? new MockLab7CommandGateway() : gateway;
        getStyleClass().add("main-view");
        setPadding(new Insets(14));

        setTop(buildTopBar());
        setLeft(buildSidebar());
        setCenter(buildContent());
        refreshData();
    }

    private HBox buildTopBar() {
        Label currentUserLabel = new Label(Messages.get(Messages.Key.CURRENT_USER) + ": " + currentUser);
        currentUserLabel.getStyleClass().add("current-user-label");
        UiButton logout = new UiButton(Messages.get(Messages.Key.LOGOUT), ButtonVariant.GHOST);
        HBox top = new HBox(12, currentUserLabel, logout);
        top.getStyleClass().add("top-bar");
        HBox.setHgrow(currentUserLabel, Priority.ALWAYS);
        return top;
    }

    private UiSidebar buildSidebar() {
        return new UiSidebar("HumanBeing")
                .addItem("Collection", () -> {})
                .addItem("Commands", () -> {})
                .addItem("Visualization", () -> {})
                .addItem("Settings", () -> {});
    }

    private VBox buildContent() {
        VBox content = new VBox(12);
        content.getStyleClass().add("main-content");

        UiAlert note = new UiAlert("Prototype", "Table + filter/sort + Canvas visualization + commands", AlertVariant.INFO);
        ResizableSplitPane split = new ResizableSplitPane(Orientation.HORIZONTAL, buildTableCard(), buildVisualizationCard()).setDivider(0.62);
        VBox.setVgrow(split, Priority.ALWAYS);

        HBox commandBar = buildCommandBar();
        content.getChildren().addAll(note, split, commandBar);
        return content;
    }

    private UiCard buildTableCard() {
        UiCard card = new UiCard("Таблица объектов", "Каждое поле HumanBeing — отдельная колонка");
        TextField filterInput = new TextField();
        filterInput.setPromptText("Фильтр по name/owner/mood...");
        filterInput.getStyleClass().add("ui-input");

        UiButton apply = new UiButton("Применить Stream filter", ButtonVariant.OUTLINE);
        UiButton reset = new UiButton("Сбросить", ButtonVariant.GHOST);
        UiButton sort = new UiButton("Sort by name", ButtonVariant.SECONDARY);

        HBox filters = new HBox(8, filterInput, apply, reset, sort);
        HBox.setHgrow(filterInput, Priority.ALWAYS);

        table.addColumn(new ColumnSpec<>("id", HumanBeingUiModel::id, 60, Comparator.comparingLong(HumanBeingUiModel::id)))
                .addColumn(new ColumnSpec<>("name", HumanBeingUiModel::name, 120, Comparator.comparing(HumanBeingUiModel::name)))
                .addColumn(new ColumnSpec<>("x", h -> h.coordinates().x(), 60))
                .addColumn(new ColumnSpec<>("y", h -> h.coordinates().y(), 60))
                .addColumn(new ColumnSpec<>("impactSpeed", HumanBeingUiModel::impactSpeed, 110))
                .addColumn(new ColumnSpec<>("minutes", HumanBeingUiModel::minutesOfWaiting, 90))
                .addColumn(new ColumnSpec<>("weaponType", HumanBeingUiModel::weaponType, 110))
                .addColumn(new ColumnSpec<>("mood", HumanBeingUiModel::mood, 100))
                .addColumn(new ColumnSpec<>("car.name", h -> h.car().name(), 110))
                .addColumn(new ColumnSpec<>("car.cool", h -> h.car().cool(), 80))
                .addColumn(new ColumnSpec<>("owner", HumanBeingUiModel::ownerLogin, 100));

        apply.setOnAction(e -> {
            String q = filterInput.getText() == null ? "" : filterInput.getText().toLowerCase(Locale.ROOT);
            table.applyFilter(h -> h.name().toLowerCase(Locale.ROOT).contains(q)
                    || h.ownerLogin().toLowerCase(Locale.ROOT).contains(q)
                    || h.mood().name().toLowerCase(Locale.ROOT).contains(q));
        });
        reset.setOnAction(e -> {
            filterInput.clear();
            table.clearFilterAndSort();
        });
        sort.setOnAction(e -> table.applySorter(Comparator.comparing(HumanBeingUiModel::name)));

        table.table().getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> showObjectInfo(selected));

        card.content().getChildren().addAll(filters, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return card;
    }

    private UiCard buildVisualizationCard() {
        UiCard card = new UiCard("Область визуализации", "Цвет зависит от owner, координаты — от coordinates.x/y");
        canvas.setHeight(420);
        canvas.setWidth(420);
        canvas.setOnObjectSelected(this::showObjectInfo);
        UiEmpty hint = new UiEmpty("Click object", "При клике по объекту информация появится ниже");
        objectInfo.setWrapText(true);
        objectInfo.getStyleClass().add("object-info");
        card.content().getChildren().addAll(canvas, hint, objectInfo);
        return card;
    }

    private HBox buildCommandBar() {
        UiButton add = new UiButton(Messages.get(Messages.Key.ADD));
        UiButton edit = new UiButton(Messages.get(Messages.Key.EDIT), ButtonVariant.OUTLINE);
        UiButton delete = new UiButton(Messages.get(Messages.Key.DELETE), ButtonVariant.DESTRUCTIVE);
        UiButton clear = new UiButton("Clear mine", ButtonVariant.OUTLINE);
        UiButton info = new UiButton("Info", ButtonVariant.GHOST);
        UiButton addIfMax = new UiButton("Add if max", ButtonVariant.GHOST);
        UiButton addIfMin = new UiButton("Add if min", ButtonVariant.GHOST);
        UiButton removeGreater = new UiButton("Remove greater", ButtonVariant.GHOST);
        UiButton refresh = new UiButton(Messages.get(Messages.Key.REFRESH), ButtonVariant.SECONDARY);

        add.setOnAction(e -> new HumanBeingFormDialog(getScene().getWindow(), "Добавить объект").showAndWait());
        edit.setOnAction(e -> {
            HumanBeingUiModel selected = table.getSelectedItem();
            if (selected == null) return;
            if (!selected.ownerLogin().equals(currentUser)) {
                objectInfo.setText("Нельзя редактировать объект другого пользователя: owner=" + selected.ownerLogin());
                return;
            }
            new HumanBeingFormDialog(getScene().getWindow(), "Редактировать объект").showAndWait();
        });

        delete.setOnAction(e -> {
            HumanBeingUiModel selected = table.getSelectedItem();
            if (selected == null) {
                objectInfo.setText("Сначала выберите объект для удаления");
                return;
            }
            if (!selected.ownerLogin().equals(currentUser)) {
                objectInfo.setText("Нельзя удалить объект другого пользователя: owner=" + selected.ownerLogin());
                return;
            }
            boolean removed = gateway.removeById(selected.id());
            objectInfo.setText(removed ? "Объект удалён: id=" + selected.id() : "Объект не был удалён");
            refreshData();
        });

        clear.setOnAction(e -> {
            int count = gateway.clearMine();
            objectInfo.setText("Команда clear выполнена. Удалено объектов: " + count);
            refreshData();
        });
        info.setOnAction(e -> objectInfo.setText(gateway.info()));
        addIfMax.setOnAction(e -> objectInfo.setText("Prototype: здесь будет команда add_if_max"));
        addIfMin.setOnAction(e -> objectInfo.setText("Prototype: здесь будет команда add_if_min"));
        removeGreater.setOnAction(e -> objectInfo.setText("Prototype: здесь будет команда remove_greater"));
        refresh.setOnAction(e -> refreshData());

        HBox bar = new HBox(8, add, edit, delete, clear, info, addIfMax, addIfMin, removeGreater, refresh);
        bar.getStyleClass().add("command-bar");
        return bar;
    }

    private void refreshData() {
        items = gateway.show();
        table.setItems(items);
        canvas.setItems(items);
    }

    private void showObjectInfo(HumanBeingUiModel h) {
        if (h == null) return;
        objectInfo.setText("id=" + h.id()
                + ", name=" + h.name()
                + ", coordinates=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                + ", owner=" + h.ownerLogin()
                + ", mood=" + h.mood()
                + ", impactSpeed=" + h.impactSpeed());
    }
}
