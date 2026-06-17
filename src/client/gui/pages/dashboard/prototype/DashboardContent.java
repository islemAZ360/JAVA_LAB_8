package client.gui.pages.dashboard.prototype;

import client.gui.components.card.UiCard;
import client.gui.components.empty.UiEmpty;
import client.gui.integration.Lab7CommandGateway;
import client.gui.model.HumanBeingUiModel;
import client.gui.mockup.HumanBeingFormDialog;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardContent extends BorderPane {
    private final String currentUser;
    private final Lab7CommandGateway gateway;

    private final HumanBeingTablePanel tablePanel;
    private final HumanBeingVisualizationPanel visualizationPanel = new HumanBeingVisualizationPanel();
    private final HumanBeingCommandBar commandBar = new HumanBeingCommandBar();
    private final Label statusLabel = new Label("Ready");
    private final UiCard commandsPanel = new UiCard();
    private final UiCard settingsPanel = new UiCard();

    private List<HumanBeingUiModel> items = List.of();
    private DashboardSection currentSection = DashboardSection.COLLECTION;

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
        showSection(DashboardSection.COLLECTION);
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

    public void showSection(DashboardSection section) {
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

    private enum CommandMode {
        ADD,
        ADD_IF_MAX,
        ADD_IF_MIN
    }
}
