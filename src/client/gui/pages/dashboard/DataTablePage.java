package client.gui.pages.dashboard;

import client.gui.components.button.ButtonVariant;
import client.gui.components.button.UiButton;
import client.gui.components.card.UiCard;
import client.gui.components.datatable.ColumnSpec;
import client.gui.components.datatable.UiDataTable;
import client.gui.components.field.UiField;
import client.gui.components.input.UiInputGroup;
import client.gui.components.select.UiSelect;
import client.gui.core.Messages;
import client.gui.integration.Lab7CommandGateway;
import client.gui.model.HumanBeingUiModel;
import client.gui.layout.BasePage;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Page 3: Data Table + Streams API.
 * Demonstrates filtering and sorting using Stream API.
 */
public class DataTablePage extends BasePage {

    private final Lab7CommandGateway gateway;
    private final UiDataTable<HumanBeingUiModel> table;

    public DataTablePage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_DATATABLE_TITLE),
                Messages.get(Messages.Key.PAGE_DATATABLE_DESCRIPTION)
        );

        this.gateway = gateway;
        this.table = createHumanTable();
        this.table.setItems(gateway.show());
        this.getStyleClass().add("data-table-page");

        buildContent();
    }

    private void buildContent() {
        // Filter controls
        UiInputGroup search = new UiInputGroup(
                "filter",
                Messages.get(Messages.Key.DATATABLE_FILTER_PLACEHOLDER),
                null
        );

        UiSelect<String> sortSelect = new UiSelect<>(List.of(
                "id", "name", "x", "y", "impactSpeed", "minutesOfWaiting", "owner"
        ));
        sortSelect.selectFirstIfAny();

        UiButton apply = new UiButton(
                Messages.get(Messages.Key.DATATABLE_APPLY),
                ButtonVariant.DEFAULT
        );
        UiButton reset = new UiButton(
                Messages.get(Messages.Key.DATATABLE_RESET),
                ButtonVariant.OUTLINE
        );
        UiButton refresh = new UiButton(
                Messages.get(Messages.Key.DATATABLE_REFRESH),
                ButtonVariant.SECONDARY
        );

        apply.setOnAction(e -> applyFilters(search.getText(), sortSelect.selected()));

        reset.setOnAction(e -> {
            search.setText("");
            table.clearFilterAndSort();
        });

        refresh.setOnAction(e -> table.setItems(gateway.show()));

        HBox controls = new HBox(10,
                new UiField(Messages.get(Messages.Key.DATATABLE_FILTER_LABEL), search),
                new UiField(Messages.get(Messages.Key.DATATABLE_SORT_LABEL), sortSelect),
                apply, reset, refresh
        );
        controls.setAlignment(Pos.BOTTOM_LEFT);

        // Table card
        UiCard tableCard = new UiCard(
                Messages.get(Messages.Key.DATATABLE_CARD_TITLE),
                Messages.get(Messages.Key.DATATABLE_CARD_DESC)
        );
        tableCard.content().getChildren().addAll(controls, table);
        setVGrow(table);

        this.getChildren().add(tableCard);
        setVGrow(tableCard);
    }

    private void applyFilters(String query, String sortCol) {
        String q = safeLower(query);

        table.applyFilter(h ->
                safeLower(h.name()).contains(q)
                        || safeLower(h.ownerLogin()).contains(q)
                        || safeLower(String.valueOf(h.weaponType())).contains(q)
        );

        table.applySorter(comparatorByColumn(sortCol));
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

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
