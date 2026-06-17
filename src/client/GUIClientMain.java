//package client.ui;
//
//import client.ui.template.core.CssLoader;
//import client.ui.template.core.Theme;
//import client.ui.template.core.ThemeManager;
//import client.ui.template.integration.AuthResult;
//import client.ui.template.integration.Lab7CommandGateway;
//import client.ui.template.integration.MockLab7CommandGateway;
//import client.ui.template.mockup.LoginView;
//import client.ui.template.mockup.MainPrototypeView;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class MainApp extends Application {
//    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();
//
//    @Override
//    public void start(Stage stage) {
//        LoginView loginView = new LoginView();
//        Scene scene = new Scene(loginView, 520, 420);
//        CssLoader.applyTo(scene);
//        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);
//
//        loginView.setOnLogin((login, password) -> {
//            AuthResult result = gateway.login(login, password);
//            if (!result.success()) {
//                loginView.showMessage(result.message());
//                return;
//            }
//            openMainWindow(scene, result.username());
//        });
//
//        loginView.setOnRegister((login, password) -> {
//            AuthResult result = gateway.register(login, password);
//            loginView.showMessage(result.message());
//            if (result.success()) {
//                openMainWindow(scene, result.username());
//            }
//        });
//
//        stage.setTitle("HumanBeing GUI Prototype");
//        stage.setMinWidth(1100);
//        stage.setMinHeight(720);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    private void openMainWindow(Scene scene, String username) {
//        String currentUser = username == null || username.isBlank() ? "demo_user" : username;
//        MainPrototypeView mainView = new MainPrototypeView(currentUser, gateway);
//        scene.setRoot(mainView);
//        ThemeManager.applyTheme(scene.getRoot(), Theme.DARK);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
package client;

import client.ui.template.components.alert.AlertVariant;
import client.ui.template.components.alert.UiAlert;
import client.ui.template.components.avatar.UiAvatar;
import client.ui.template.components.button.ButtonSize;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.components.card.UiCard;
import client.ui.template.components.datatable.ColumnSpec;
import client.ui.template.components.datatable.UiDataTable;
import client.ui.template.components.dialog.UiDialog;
import client.ui.template.components.empty.UiEmpty;
import client.ui.template.components.field.UiField;
import client.ui.template.components.input.UiInputGroup;
import client.ui.template.components.resizable.ResizableSplitPane;
import client.ui.template.components.select.UiSelect;
import client.ui.template.components.sidebar.UiSidebar;
import client.ui.template.components.spinner.UiSpinner;
import client.ui.template.core.CssLoader;
import client.ui.template.core.Direction;
import client.ui.template.core.Messages;
import client.ui.template.core.RtlSupport;
import client.ui.template.core.Theme;
import client.ui.template.core.ThemeManager;
import client.ui.template.integration.AuthResult;
import client.ui.template.integration.Lab7CommandGateway;
import client.ui.template.integration.MockLab7CommandGateway;
import client.ui.template.mockup.LoginView;
import client.ui.template.mockup.MainPrototypeView;
import client.ui.template.mockup.ObjectVisualizationCanvas;
import client.ui.template.model.HumanBeingUiModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class GUIClientMain extends Application {
    private static int current_timeout_index = 0;
    private static final int[] timeouts = {5, 15, 30, 60, 120, 300, 600};
    private static final int BASE_TIMEOUT = 5;

    private static Socket socket = null;
    private static volatile boolean isConnected = false;
    private static volatile boolean isReconnecting = false;

    private static DataOutputStream dos;
    private static DataInputStream dis = null;
    private static RequestSender reqSender = null;
    private static InputManager inputMng = null;
    private static Scanner scanner = new Scanner(System.in);

    private static ScheduledFuture<?> currentTask;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();

    private ConfigLoader configLoader = new ConfigLoader("ui/resources/config/config.properties");
    private Theme currentTheme = Theme.DARK;
    private Direction currentDirection = Direction.LTR;
    private String currentUser = "demo_user";

    @Override
    public void start(Stage stage) {
        stage.setTitle(configLoader.getString("window.title"));
        stage.setMinWidth(configLoader.getInt("window.minWidth"));
        stage.setMinHeight(configLoader.getInt("window.minHeight"));

        StackPane rootLayout = new StackPane(); // <div id="root"></div>
        rootLayout.setMaxWidth(Double.MAX_VALUE);
        rootLayout.setMaxHeight(Double.MAX_VALUE);
//        rootLayout.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        Scene scene = new Scene(rootLayout, configLoader.getInt("window.width"), configLoader.getInt("window.height"));
        CssLoader.applyTo(scene);
        stage.setScene(scene);

        openLoginWindow(stage);

//        stage.sizeToScene();
        stage.show();
    }

    private void openLoginWindow(Stage stage) {
        LoginView loginView = new LoginView();
        loginView.setMaxWidth(520);
        loginView.setMaxHeight(420);

        // DEBUG
        System.out.println("--- KÍCH THƯỚC BAN ĐẦU ---");
        System.out.println("Cấu hình Max Height: " + loginView.getMaxHeight());
        System.out.println("Cấu hình Pref Height: " + loginView.getPrefHeight());

        loginView.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("[DEBUG] Chiều rộng thực tế của LoginView: " + newValue);
        });

        loginView.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("[DEBUG] Chiều cao thực tế của LoginView: " + newValue);
        });

        Scene scene = stage.getScene();
        if (scene == null) {
            CssLoader.applyTo(scene);
            stage.setScene(scene);
        } else {
            scene.setRoot(loginView);
        }

        applyUiSettings(scene.getRoot());

        loginView.setOnLogin((login, password) -> {
            AuthResult result = gateway.login(login, password);
            if (!result.success()) {
                loginView.showMessage(result.message());
                return;
            }
            currentUser = normalizeUsername(result.username());
            openMainDashboard(stage);
        });

        loginView.setOnRegister((login, password) -> {
            AuthResult result = gateway.register(login, password);
            loginView.showMessage(result.message());
            if (result.success()) {
                currentUser = normalizeUsername(result.username());
                openMainDashboard(stage);
            }
        });

    }

    private void openMainDashboard(Stage stage) {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("app-shell");

        StackPane contentHost = new StackPane();
        contentHost.getStyleClass().add("app-content-host");

        UiSidebar sidebar = buildSidebar(stage, contentHost);

        shell.setLeft(sidebar);
        shell.setCenter(contentHost);

        Scene scene = stage.getScene();
        scene.setRoot(shell);
        applyUiSettings(shell);

        showPrototype(contentHost);
    }

    private UiSidebar buildSidebar(Stage stage, StackPane contentHost) {
        UiAvatar avatar = new UiAvatar(currentUser);
        Label userLabel = new Label(Messages.get(Messages.Key.CURRENT_USER) + ": " + currentUser);
        userLabel.getStyleClass().add("current-user-label");

        UiButton logout = new UiButton(Messages.get(Messages.Key.LOGOUT), ButtonVariant.OUTLINE);
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> openLoginWindow(stage));

        VBox footer = new VBox(10, avatar, userLabel, logout);
        footer.setAlignment(Pos.CENTER_LEFT);

        return new UiSidebar("HumanBeing GUI")
                .addItem("1. Lab 8 Prototype", () -> showPrototype(contentHost))
                .addItem("2. UI Component System", () -> showComponentSystem(contentHost))
                .addItem("3. Data Table + Streams", () -> showDataTablePage(contentHost))
                .addItem("4. Canvas Visualization", () -> showVisualizationPage(contentHost))
                .addItem("5. Theme / RTL / i18n", () -> showSettingsPage(contentHost))
                .setFooter(footer);
    }

    private void showPrototype(StackPane contentHost) {
        MainPrototypeView prototypeView = new MainPrototypeView(currentUser, gateway);
        setContent(contentHost, prototypeView);
    }

    private void showComponentSystem(StackPane contentHost) {
        VBox page = createPage(
                "UI Component System",
                "Полная витрина JavaFX-компонентов: Card, Empty, Alert, Button, InputGroup, Field, Resizable, Avatar, Spinner, DataTable, Dialog, Select, Sidebar."
        );

        page.getChildren().addAll(
                buildAlertShowcase(),
                buildButtonShowcase(),
                buildFormShowcase(),
                buildCardEmptyAvatarSpinnerShowcase(contentHost),
                buildResizableDialogShowcase(contentHost),
                buildSmallTableShowcase()
        );

        setContent(contentHost, wrapScroll(page));
    }

    private UiCard buildAlertShowcase() {
        UiCard card = new UiCard("Alert", "Компонент для сообщений разных типов.");

        FlowPane alerts = new FlowPane(12, 12);
        alerts.getChildren().addAll(
                new UiAlert("Info", "Обычное информационное сообщение.", AlertVariant.INFO),
                new UiAlert("Success", "Операция выполнена успешно.", AlertVariant.SUCCESS),
                new UiAlert("Warning", "Проверьте введённые данные.", AlertVariant.WARNING),
                new UiAlert("Error", "Произошла ошибка выполнения команды.", AlertVariant.ERROR)
        );

        card.content().getChildren().add(alerts);
        return card;
    }

    private UiCard buildButtonShowcase() {
        UiCard card = new UiCard("Button", "Варианты кнопок и размеры.");

        FlowPane variants = new FlowPane(10, 10);
        variants.getChildren().addAll(
                new UiButton("Default", ButtonVariant.DEFAULT),
                new UiButton("Secondary", ButtonVariant.SECONDARY),
                new UiButton("Outline", ButtonVariant.OUTLINE),
                new UiButton("Ghost", ButtonVariant.GHOST),
                new UiButton("Delete", ButtonVariant.DESTRUCTIVE)
        );

        FlowPane sizes = new FlowPane(10, 10);
        sizes.getChildren().addAll(
                new UiButton("Small").applySize(ButtonSize.SMALL),
                new UiButton("Default").applySize(ButtonSize.DEFAULT),
                new UiButton("Large").applySize(ButtonSize.LARGE),
                new UiButton("★").applySize(ButtonSize.ICON)
        );

        card.content().getChildren().addAll(
                new Label("Variants:"),
                variants,
                new Label("Sizes:"),
                sizes
        );

        return card;
    }

    private UiCard buildFormShowcase() {
        UiCard card = new UiCard("Field / InputGroup / Select", "Компоненты для форм добавления и редактирования HumanBeing.");

        UiInputGroup nameInput = new UiInputGroup("name", "Введите имя объекта", null);
        UiInputGroup xInput = new UiInputGroup("x", "coordinates.x", null);
        UiInputGroup yInput = new UiInputGroup("y", "coordinates.y", null);

        UiSelect<String> weaponSelect = new UiSelect<>(List.of(
                "HAMMER",
                "SHOTGUN",
                "KNIFE",
                "MACHINE_GUN",
                "BAT"
        ));
        weaponSelect.selectFirstIfAny();

        UiSelect<String> moodSelect = new UiSelect<>(List.of(
                "SADNESS",
                "LONGING",
                "GLOOM",
                "CALM",
                "RAGE"
        ));
        moodSelect.selectFirstIfAny();

        CheckBox realHero = new CheckBox("realHero");
        CheckBox hasToothpick = new CheckBox("hasToothpick");

        HBox booleans = new HBox(12, realHero, hasToothpick);

        card.content().getChildren().addAll(
                new UiField("name", nameInput).setHelper("Поле name обязательно."),
                new UiField("coordinates.x", xInput),
                new UiField("coordinates.y", yInput),
                new UiField("weaponType", weaponSelect),
                new UiField("mood", moodSelect),
                new UiField("boolean fields", booleans)
        );

        return card;
    }

    private UiCard buildCardEmptyAvatarSpinnerShowcase(StackPane contentHost) {
        UiCard card = new UiCard("Card / Empty / Avatar / Spinner", "Базовые компоненты интерфейса.");

        UiAvatar avatar = new UiAvatar(currentUser).setRadius(24);
        UiSpinner spinner = new UiSpinner().setSize(42);

        UiEmpty empty = new UiEmpty(
                Messages.get(Messages.Key.EMPTY_TITLE),
                Messages.get(Messages.Key.EMPTY_DESCRIPTION)
        );
        empty.addAction("Refresh", () -> showComponentSystem(contentHost));

        HBox top = new HBox(16, avatar, spinner);
        top.setAlignment(Pos.CENTER_LEFT);

        card.content().getChildren().addAll(top, empty);
        card.footer().getChildren().add(new UiButton("Footer action", ButtonVariant.OUTLINE));

        return card;
    }

    private UiCard buildResizableDialogShowcase(StackPane contentHost) {
        UiCard card = new UiCard("Resizable / Dialog", "SplitPane и модальное окно.");

        UiCard left = new UiCard("Left panel", "Например таблица объектов.");
        left.content().getChildren().add(new Label("Table area"));

        UiCard right = new UiCard("Right panel", "Например область визуализации.");
        right.content().getChildren().add(new Label("Canvas area"));

        ResizableSplitPane split = new ResizableSplitPane(Orientation.HORIZONTAL, left, right)
                .setDivider(0.5);
        split.setPrefHeight(180);

        UiButton openDialog = new UiButton("Open Dialog", ButtonVariant.DEFAULT);
        openDialog.setOnAction(e -> {
            UiDialog dialog = new UiDialog("Demo Dialog", contentHost.getScene().getWindow());

            UiAlert alert = new UiAlert(
                    "Dialog component",
                    "Это пример модального окна. Позже здесь будет форма add/update HumanBeing.",
                    AlertVariant.INFO
            );

            UiButton close = new UiButton("Close", ButtonVariant.SECONDARY);
            close.setOnAction(closeEvent -> dialog.close());

            dialog.content().getChildren().add(alert);
            dialog.footer().getChildren().add(close);
            dialog.showAndWait();
        });

        card.content().getChildren().addAll(split, openDialog);
        return card;
    }

    private UiCard buildSmallTableShowcase() {
        UiCard card = new UiCard("Data Table", "Короткая таблица внутри component showcase.");

        UiDataTable<HumanBeingUiModel> table = createHumanTable();
        table.setItems(gateway.show());

        table.setMaxHeight(260);
        card.content().getChildren().add(table);

        return card;
    }

    private void showDataTablePage(StackPane contentHost) {
        VBox page = createPage(
                "Data Table + Streams API",
                "Фильтрация и сортировка выполняются на стороне GUI через Stream API."
        );

        UiDataTable<HumanBeingUiModel> table = createHumanTable();
        table.setItems(gateway.show());

        UiInputGroup search = new UiInputGroup("filter", "name / owner / mood", null);

        UiSelect<String> sortSelect = new UiSelect<>(List.of(
                "id",
                "name",
                "x",
                "y",
                "impactSpeed",
                "minutesOfWaiting",
                "owner"
        ));
        sortSelect.selectFirstIfAny();

        UiButton apply = new UiButton("Apply Stream filter/sort", ButtonVariant.DEFAULT);
        UiButton reset = new UiButton("Reset", ButtonVariant.OUTLINE);
        UiButton refresh = new UiButton("Refresh from gateway", ButtonVariant.SECONDARY);

        apply.setOnAction(e -> {
            String q = safeLower(search.getText());

            table.applyFilter(h -> safeLower(h.name()).contains(q)
                    || safeLower(h.ownerLogin()).contains(q)
                    || safeLower(String.valueOf(h.mood())).contains(q)
                    || safeLower(String.valueOf(h.weaponType())).contains(q));

            table.applySorter(comparatorByColumn(sortSelect.selected()));
        });

        reset.setOnAction(e -> {
            search.setText("");
            table.clearFilterAndSort();
        });

        refresh.setOnAction(e -> table.setItems(gateway.show()));

        HBox controls = new HBox(10,
                new UiField("Filter", search),
                new UiField("Sort column", sortSelect),
                apply,
                reset,
                refresh
        );
        controls.setAlignment(Pos.BOTTOM_LEFT);

        UiCard tableCard = new UiCard(
                "HumanBeing collection",
                "Каждое поле объекта показано отдельной колонкой."
        );
        tableCard.content().getChildren().addAll(controls, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        page.getChildren().add(tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        setContent(contentHost, page);
    }

    private void showVisualizationPage(StackPane contentHost) {
        VBox page = createPage(
                "Canvas Visualization",
                "Объекты рисуются графическими примитивами. Координаты берутся из coordinates.x/y, размер — из impactSpeed, цвет — из ownerLogin."
        );

        ObjectVisualizationCanvas canvas = new ObjectVisualizationCanvas();
        canvas.setWidth(760);
        canvas.setHeight(460);
        canvas.setItems(gateway.show());

        Label info = new Label("Click object on canvas");
        info.setWrapText(true);
        info.getStyleClass().add("object-info");

        canvas.setOnObjectSelected(h -> info.setText(
                "id=" + h.id()
                        + ", name=" + h.name()
                        + ", owner=" + h.ownerLogin()
                        + ", coordinates=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                        + ", impactSpeed=" + h.impactSpeed()
        ));

        UiAlert note = new UiAlert(
                "Animation",
                "При обновлении данных Canvas запускает простую анимацию появления объектов.",
                AlertVariant.INFO
        );

        UiButton reload = new UiButton("Reload and animate", ButtonVariant.DEFAULT);
        reload.setOnAction(e -> canvas.setItems(gateway.show()));

        UiCard canvasCard = new UiCard("Visualization area", "Interactive Canvas");
        canvasCard.content().getChildren().addAll(canvas, info, reload);

        page.getChildren().addAll(note, canvasCard);

        setContent(contentHost, wrapScroll(page));
    }

    private void showSettingsPage(StackPane contentHost) {
        VBox page = createPage(
                "Theme / RTL / i18n",
                "Демонстрация переключения темы, направления интерфейса и языка."
        );

        UiSelect<Theme> themeSelect = new UiSelect<>(List.of(Theme.DARK, Theme.LIGHT, Theme.CYBERPUNK));
        themeSelect.getSelectionModel().select(currentTheme);

        UiButton applyTheme = new UiButton("Apply Theme", ButtonVariant.DEFAULT);
        applyTheme.setOnAction(e -> {
            Theme selectedTheme = themeSelect.selected();
            if (selectedTheme != null) {
                currentTheme = selectedTheme;
                applyUiSettings(contentHost.getScene().getRoot());
            }
        });

        UiSelect<Direction> directionSelect = new UiSelect<>(List.of(Direction.LTR, Direction.RTL));
        directionSelect.getSelectionModel().select(currentDirection);

        UiButton applyDirection = new UiButton("Apply Direction", ButtonVariant.DEFAULT);
        applyDirection.setOnAction(e -> {
            Direction selectedDirection = directionSelect.selected();
            if (selectedDirection != null) {
                currentDirection = selectedDirection;
                applyUiSettings(contentHost.getScene().getRoot());
            }
        });

        UiSelect<Messages.Lang> langSelect = new UiSelect<>(List.of(
                Messages.Lang.RU,
                Messages.Lang.EN_CA,
                Messages.Lang.VI
        ));
        langSelect.getSelectionModel().select(Messages.getCurrentLang());

        Label langPreview = new Label(buildLangPreview());
        langPreview.setWrapText(true);

        UiButton applyLang = new UiButton("Apply Language", ButtonVariant.DEFAULT);
        applyLang.setOnAction(e -> {
            Messages.Lang selectedLang = langSelect.selected();
            if (selectedLang != null) {
                Messages.setLang(selectedLang);
                langPreview.setText(buildLangPreview());
            }
        });

        UiCard themeCard = new UiCard("Theme", "Looked-up colors через CSS-классы theme-dark/theme-light/theme-cyberpunk.");
        themeCard.content().getChildren().addAll(
                new UiField("Theme", themeSelect),
                applyTheme
        );

        UiCard directionCard = new UiCard("RTL", "Переключение NodeOrientation для всего root.");
        directionCard.content().getChildren().addAll(
                new UiField("Direction", directionSelect),
                applyDirection
        );

        UiCard langCard = new UiCard("i18n", "Тексты берутся из Java class Messages.");
        langCard.content().getChildren().addAll(
                new UiField("Language", langSelect),
                applyLang,
                langPreview
        );

        page.getChildren().addAll(themeCard, directionCard, langCard);

        setContent(contentHost, wrapScroll(page));
    }

    private UiDataTable<HumanBeingUiModel> createHumanTable() {
        return new UiDataTable<HumanBeingUiModel>()
                .addColumn(new ColumnSpec<>("id", HumanBeingUiModel::id, 60,
                        Comparator.comparingLong(HumanBeingUiModel::id)))
                .addColumn(new ColumnSpec<>("name", HumanBeingUiModel::name, 120,
                        Comparator.comparing(HumanBeingUiModel::name)))
                .addColumn(new ColumnSpec<>("x", h -> h.coordinates().x(), 60))
                .addColumn(new ColumnSpec<>("y", h -> h.coordinates().y(), 60))
                .addColumn(new ColumnSpec<>("creationDate", HumanBeingUiModel::creationDate, 150))
                .addColumn(new ColumnSpec<>("realHero", HumanBeingUiModel::realHero, 90))
                .addColumn(new ColumnSpec<>("hasToothpick", HumanBeingUiModel::hasToothpick, 110))
                .addColumn(new ColumnSpec<>("impactSpeed", HumanBeingUiModel::impactSpeed, 110))
                .addColumn(new ColumnSpec<>("minutes", HumanBeingUiModel::minutesOfWaiting, 90))
                .addColumn(new ColumnSpec<>("weaponType", HumanBeingUiModel::weaponType, 120))
                .addColumn(new ColumnSpec<>("mood", HumanBeingUiModel::mood, 100))
                .addColumn(new ColumnSpec<>("car.name", h -> h.car().name(), 110))
                .addColumn(new ColumnSpec<>("car.cool", h -> h.car().cool(), 90))
                .addColumn(new ColumnSpec<>("owner", HumanBeingUiModel::ownerLogin, 110));
    }

    private Comparator<HumanBeingUiModel> comparatorByColumn(String column) {
        if (column == null) {
            return Comparator.comparingLong(HumanBeingUiModel::id);
        }

        return switch (column) {
            case "name" -> Comparator.comparing(HumanBeingUiModel::name);
            case "x" -> Comparator.comparingInt(h -> h.coordinates().x());
            case "y" -> Comparator.comparingLong(h -> h.coordinates().y());
            case "impactSpeed" -> Comparator.comparingDouble(HumanBeingUiModel::impactSpeed);
            case "minutesOfWaiting" -> Comparator.comparingLong(HumanBeingUiModel::minutesOfWaiting);
            case "owner" -> Comparator.comparing(HumanBeingUiModel::ownerLogin);
            default -> Comparator.comparingLong(HumanBeingUiModel::id);
        };
    }

    private VBox createPage(String titleText, String descriptionText) {
        VBox page = new VBox(16);
        page.setPadding(new Insets(18));
        page.getStyleClass().add("page");

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");

        Label description = new Label(descriptionText);
        description.getStyleClass().add("page-description");
        description.setWrapText(true);

        page.getChildren().addAll(title, description);
        return page;
    }

    private ScrollPane wrapScroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        return scrollPane;
    }

    private void setContent(StackPane contentHost, Node content) {
        contentHost.getChildren().setAll(content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    private void applyUiSettings(Parent root) {
        ThemeManager.applyTheme(root, currentTheme);
        RtlSupport.applyToRoot(root, currentDirection);
    }

    private String normalizeUsername(String username) {
        return username == null || username.isBlank() ? "demo_user" : username;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String buildLangPreview() {
        return Messages.get(Messages.Key.LOGIN_TITLE) + " | "
                + Messages.get(Messages.Key.ADD) + " | "
                + Messages.get(Messages.Key.EDIT) + " | "
                + Messages.get(Messages.Key.DELETE) + " | "
                + Messages.get(Messages.Key.LOGOUT);
    }

    public static void main(String[] args) {
        launch(args);
    }
}