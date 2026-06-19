package main.java.client.gui.mockup.dashboard;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.datatable.ColumnSpec;
import main.java.client.gui.components.datatable.UiDataTable;
import main.java.client.gui.core.Messages;
import main.java.client.gui.core.OwnerColorPalette;
import main.java.client.gui.model.HumanBeingUiModel;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;

public class HumanBeingTablePanel extends UiCard {
    private final UiDataTable<HumanBeingUiModel> table = new UiDataTable<>();
    private final TextField filterInput = new TextField();
    private final UiButton applyFilterButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton resetButton = new UiButton("", ButtonVariant.GHOST);
    private final UiButton sortButton = new UiButton("", ButtonVariant.SECONDARY);
    private final Consumer<String> statusConsumer;

    public HumanBeingTablePanel(Consumer<String> statusConsumer) {
        this.statusConsumer = statusConsumer;

        configureControls();
        configureTableColumns();
        configureLayout();
        refreshLanguage();
    }

    public void setItems(Collection<HumanBeingUiModel> items) {
        table.setItems(items);
    }

    public HumanBeingUiModel getSelectedItem() {
        return table.getSelectedItem();
    }

    public UiDataTable<HumanBeingUiModel> dataTable() {
        return table;
    }

    public void select(HumanBeingUiModel item) {
        table.table().getSelectionModel().select(item);
    }

    public void setOnSelectionChanged(ChangeListener<HumanBeingUiModel> listener) {
        table.table().getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public void refreshLanguage() {
        setTitle(Messages.get(Messages.Key.OBJECT_TABLE_TITLE));
        setDescription(Messages.get(Messages.Key.OBJECT_TABLE_DESCRIPTION));
        filterInput.setPromptText(Messages.get(Messages.Key.FILTER_PROMPT));
        applyFilterButton.setText(Messages.get(Messages.Key.APPLY_STREAM_FILTER));
        resetButton.setText(Messages.get(Messages.Key.RESET));
        sortButton.setText(Messages.get(Messages.Key.SORT_BY_NAME));
    }

    private void configureControls() {
        filterInput.getStyleClass().add("ui-input");

        applyFilterButton.setOnAction(e -> {
            String q = filterInput.getText() == null
                    ? ""
                    : filterInput.getText().toLowerCase(Locale.ROOT);

            table.applyFilter(h -> contains(h.name(), q)
                    || contains(h.ownerLogin(), q)
                    || contains(String.valueOf(h.weaponType()), q)
                    || contains(h.car().name(), q));

            setStatus("Filter выполнен через Streams API");
        });

        resetButton.setOnAction(e -> {
            filterInput.clear();
            table.clearFilterAndSort();
            setStatus("Filter/sort сброшены");
        });

        sortButton.setOnAction(e -> {
            table.applySorter(Comparator.comparing(HumanBeingUiModel::name));
            setStatus("Sort by name выполнен через Streams API");
        });
    }

    private void configureLayout() {
        HBox filters = new HBox(8, filterInput, applyFilterButton, resetButton, sortButton);
        filters.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(filterInput, Priority.ALWAYS);

        content().getChildren().addAll(filters, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void configureTableColumns() {
        table.addColumn(new ColumnSpec<>("id", HumanBeingUiModel::id, 60, Comparator.comparingLong(HumanBeingUiModel::id)))
                .addColumn(new ColumnSpec<>("name", HumanBeingUiModel::name, 115, Comparator.comparing(HumanBeingUiModel::name)))
                .addColumn(new ColumnSpec<>("x", h -> h.coordinates().x(), 50))
                .addColumn(new ColumnSpec<>("y", h -> h.coordinates().y(), 50))
                .addColumn(new ColumnSpec<>("creationDate", HumanBeingUiModel::creationDate, 140))
                .addColumn(new ColumnSpec<>("realHero", HumanBeingUiModel::realHero, 80))
                .addColumn(new ColumnSpec<>("hasToothpick", HumanBeingUiModel::hasToothpick, 110))
                .addColumn(new ColumnSpec<>("impactSpeed", HumanBeingUiModel::impactSpeed, 100))
                .addColumn(new ColumnSpec<>("minutes", HumanBeingUiModel::minutesOfWaiting, 80))
                .addColumn(new ColumnSpec<>("weaponType", HumanBeingUiModel::weaponType, 110))
                .addColumn(new ColumnSpec<>("car.name", h -> h.car().name(), 100))
                .addColumn(new ColumnSpec<>("car.cool", h -> h.car().cool(), 75))
                .addColumn(new ColumnSpec<>("owner", HumanBeingUiModel::ownerLogin, 100),
                        (row, owner) -> ownerCellStyle(String.valueOf(owner)));

        table.setRowStyle(h -> "-fx-background-color: "
                + OwnerColorPalette.toRgba(OwnerColorPalette.colorFor(h.ownerLogin()), 0.10) + ";");
    }

    private String ownerCellStyle(String owner) {
        Color bg = OwnerColorPalette.colorFor(owner);
        Color fg = OwnerColorPalette.textFor(bg);

        return "-fx-background-color: " + OwnerColorPalette.toRgb(bg) + ";"
                + "-fx-text-fill: " + OwnerColorPalette.toRgb(fg) + ";"
                + "-fx-font-weight: 700;";
    }

    private boolean contains(String value, String query) {
        return query == null
                || query.isBlank()
                || value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void setStatus(String status) {
        if (statusConsumer != null) {
            statusConsumer.accept(status);
        }
    }
}
