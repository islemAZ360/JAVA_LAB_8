package main.java.client.gui.mockup.dashboard;

import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.empty.UiEmpty;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.model.HumanBeingUiModel;
import main.java.client.gui.mockup.HumanBeingFormDialog;
import main.java.client.gui.pages.dashboard.prototype.DashboardSection;
import main.java.client.gui.pages.dashboard.prototype.HumanBeingCommandBar;
import main.java.client.gui.pages.dashboard.prototype.HumanBeingTablePanel;
import main.java.client.gui.pages.dashboard.prototype.HumanBeingVisualizationPanel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardContent extends BorderPane {
    private final String currentUser;
    private final Lab7CommandGateway gateway;

    private final main.java.client.gui.pages.dashboard.prototype.HumanBeingTablePanel tablePanel;
    private final main.java.client.gui.pages.dashboard.prototype.HumanBeingVisualizationPanel visualizationPanel = new HumanBeingVisualizationPanel();
    private final main.java.client.gui.pages.dashboard.prototype.HumanBeingCommandBar commandBar = new HumanBeingCommandBar();
    private final Label statusLabel = new Label("Ready");
    private final UiCard commandsPanel = new UiCard();
    private final UiCard settingsPanel = new UiCard();

    private List<HumanBeingUiModel> items = List.of();
    private main.java.client.gui.pages.dashboard.prototype.DashboardSection currentSection = main.java.client.gui.pages.dashboard.prototype.DashboardSection.COLLECTION;

    // фоновый опрос коллекции: раз в 3 сек тянет свежие данные с сервера
    private final ScheduledExecutorService autoRefreshExecutor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "dashboard-auto-refresh");
                t.setDaemon(true);
                return t;
            });
    private static final long AUTO_REFRESH_INTERVAL_SEC = 3;
    private volatile boolean autoRefreshRunning = true;

    public DashboardContent(String currentUser, Lab7CommandGateway gateway) {
        this.currentUser = currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
        this.gateway = gateway;
        this.tablePanel = new HumanBeingTablePanel(this::setStatus);

        getStyleClass().add("main-content");
        setPadding(new Insets(14));

        configureSelectionSync();
        configureCommandActions();
        configureSecondaryPanels();
        buildLayout();

        refreshData("Данные загружены из gateway");
        showSection(main.java.client.gui.pages.dashboard.prototype.DashboardSection.COLLECTION);

        // запускаем автообновление; при logout дашборд уходит со сцены — поток гаснет сам
        startAutoRefresh();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) dispose();
        });
    }

    public void setStatus(String status) {
        statusLabel.setText(status == null || status.isBlank() ? "Ready" : status);
    }

    public void refreshLanguage() {
        tablePanel.refreshLanguage();
        visualizationPanel.refreshLanguage();
        commandBar.refreshLanguage();
        configureSecondaryPanels();
    }

    public void showSection(main.java.client.gui.pages.dashboard.prototype.DashboardSection section) {
        currentSection = section == null ? DashboardSection.COLLECTION : section;
        Node centerNode = switch (currentSection) {
            case COLLECTION -> tablePanel;
            case VISUALIZATION -> visualizationPanel;
            case COMMANDS -> commandsPanel;
            case SETTINGS -> settingsPanel;
        };
        setCenter(centerNode);
    }

    private void buildLayout() {
        statusLabel.getStyleClass().add("status-label");

        VBox bottomBox = new VBox(10, commandBar, statusLabel);
        bottomBox.setPadding(new Insets(12, 0, 0, 0));

        setBottom(bottomBox);
    }

    private void configureSelectionSync() {
        tablePanel.setOnSelectionChanged((obs, old, selected) -> {
            visualizationPanel.setSelectedObject(selected);
        });

        visualizationPanel.setOnObjectSelected(selected -> {
            tablePanel.select(selected);
            visualizationPanel.showObjectInfo(selected);
        });

        commandBar.bindSelection(tablePanel.dataTable().table().getSelectionModel().selectedItemProperty());
    }

    private void configureSecondaryPanels() {
        commandsPanel.setTitle("Commands");
        commandsPanel.setDescription("All previous lab commands are accessible through the bottom action bar.");
        commandsPanel.content().getChildren().setAll(new UiEmpty(
                "Commands via GUI",
                "Use the buttons below: add, edit, delete, clear mine, info, add if max, add if min, remove greater, refresh."
        ));

        settingsPanel.setTitle("Settings");
        settingsPanel.setDescription("Language and theme are controlled in the top bar.");
        settingsPanel.content().getChildren().setAll(new UiEmpty(
                "Settings",
                "Use the top bar to switch language and theme."
        ));
    }

    private void configureCommandActions() {
        commandBar.setOnAdd(() -> addByDialog("Добавить объект", CommandMode.ADD));
        commandBar.setOnEdit(this::editSelected);
        commandBar.setOnDelete(this::deleteSelected);

        commandBar.setOnClearMine(() -> {
            int count = gateway.clearMine();
            refreshData("Команда clear выполнена. Удалено объектов: " + count);
        });

        commandBar.setOnInfo(() -> setStatus(gateway.info()));
        commandBar.setOnAddIfMax(() -> addByDialog("Add if max", CommandMode.ADD_IF_MAX));
        commandBar.setOnAddIfMin(() -> addByDialog("Add if min", CommandMode.ADD_IF_MIN));
        commandBar.setOnRemoveGreater(this::removeGreaterSelected);
        commandBar.setOnRefresh(() -> refreshData("Данные обновлены"));
    }

    private void addByDialog(String title, CommandMode mode) {
        HumanBeingFormDialog dialog = new HumanBeingFormDialog(
                getScene().getWindow(),
                title,
                null,
                currentUser,
                nextId()
        );

        dialog.showAndWait();

        HumanBeingUiModel created = dialog.result();
        if (created == null) {
            setStatus("Команда отменена");
            return;
        }

        HumanBeingUiModel result = switch (mode) {
            case ADD -> gateway.add(created);
            case ADD_IF_MAX -> gateway.addIfMax(created);
            case ADD_IF_MIN -> gateway.addIfMin(created);
        };

        refreshData(result == null
                ? "Условие команды не выполнено: объект не добавлен"
                : "Команда выполнена: добавлен объект id=" + result.id());
    }

    private void editSelected() {
        HumanBeingUiModel selected = tablePanel.getSelectedItem();
        if (selected == null) return;

        if (!selected.ownerLogin().equals(currentUser)) {
            setStatus("Нельзя редактировать объект другого пользователя: owner=" + selected.ownerLogin());
            return;
        }

        HumanBeingFormDialog dialog = new HumanBeingFormDialog(
                getScene().getWindow(),
                "Редактировать объект",
                selected,
                currentUser,
                selected.id()
        );

        dialog.showAndWait();

        HumanBeingUiModel updated = dialog.result();
        if (updated == null) {
            setStatus("Редактирование отменено");
            return;
        }

        gateway.update(selected.id(), updated);
        refreshData("Объект изменён: id=" + updated.id());
    }

    private void deleteSelected() {
        HumanBeingUiModel selected = tablePanel.getSelectedItem();
        if (selected == null) return;

        if (!selected.ownerLogin().equals(currentUser)) {
            setStatus("Нельзя удалить объект другого пользователя: owner=" + selected.ownerLogin());
            return;
        }

        boolean removed = gateway.removeById(selected.id());
        refreshData(removed ? "Объект удалён: id=" + selected.id() : "Объект не был удалён");
    }

    private void removeGreaterSelected() {
        HumanBeingUiModel selected = tablePanel.getSelectedItem();
        if (selected == null) return;

        int count = gateway.removeGreater(selected);
        refreshData("Команда remove_greater выполнена. Удалено объектов: " + count);
    }

    private void refreshData(String status) {
        items = gateway.show();
        tablePanel.setItems(items);
        visualizationPanel.setItems(items);
        setStatus(status);
        showSection(currentSection);
    }

    private long nextId() {
        return items.stream().mapToLong(HumanBeingUiModel::id).max().orElse(0) + 1;
    }

    // фоновый поток: дёргаем gateway.show() НЕ в FX-потоке, раз в 3 секунды
    private void startAutoRefresh() {
        autoRefreshExecutor.scheduleAtFixedRate(() -> {
            if (!autoRefreshRunning) return;
            try {
                List<HumanBeingUiModel> fresh = gateway.show();
                if (!autoRefreshRunning) return;
                // UI трогаем только через runLater — иначе упадёт
                Platform.runLater(() -> applyAutoRefreshedItems(fresh));
            } catch (Exception e) {
                // Игнорируем ошибку сети: повторная попытка будет выполнена через 3 секунды
            }
        }, AUTO_REFRESH_INTERVAL_SEC, AUTO_REFRESH_INTERVAL_SEC, TimeUnit.SECONDS);
    }

    // применяем свежие данные в FX-потоке, но выделение не сбрасываем
    private void applyAutoRefreshedItems(List<HumanBeingUiModel> fresh) {
        if (!autoRefreshRunning) return;

        HumanBeingUiModel selected = tablePanel.getSelectedItem();
        long selectedId = selected == null ? -1L : selected.id();

        items = fresh == null ? List.of() : fresh;
        tablePanel.setItems(items);
        visualizationPanel.setItems(items);

        // если объект ещё жив — возвращаем выделение в таблице и на канвасе
        if (selectedId > 0) {
            HumanBeingUiModel stillThere = items.stream()
                    .filter(h -> h.id() == selectedId)
                    .findFirst()
                    .orElse(null);
            if (stillThere != null) {
                tablePanel.select(stillThere);
                visualizationPanel.setSelectedObject(stillThere);
            }
        }
    }

    // гасим фоновый поток при логауте/закрытии дашборда
    public void dispose() {
        autoRefreshRunning = false;
        autoRefreshExecutor.shutdownNow();
        try {
            autoRefreshExecutor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private enum CommandMode {
        ADD,
        ADD_IF_MAX,
        ADD_IF_MIN
    }
}
