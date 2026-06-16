package client.ui.template.components.datatable;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UiDataTable<T> extends VBox {
    private final TableView<T> table = new TableView<>();
    private final ObservableList<T> allItems = FXCollections.observableArrayList();
    private Predicate<T> filter = item -> true;
    private Comparator<T> sorter = null;

    public UiDataTable() {
        getStyleClass().add("ui-data-table-wrap");
        table.getStyleClass().add("ui-data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getChildren().add(table);
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
    }

    public UiDataTable<T> addColumn(ColumnSpec<T, ?> spec) {
        TableColumn<T, Object> column = new TableColumn<>(spec.title());
        column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Object>(spec.accessor().apply(data.getValue())));
        if (spec.width() > 0) column.setPrefWidth(spec.width());
        table.getColumns().add(column);
        return this;
    }

    public UiDataTable<T> setItems(Collection<T> items) {
        allItems.setAll(items == null ? List.of() : items);
        refreshView();
        return this;
    }

    public UiDataTable<T> applyFilter(Predicate<T> filter) {
        this.filter = filter == null ? item -> true : filter;
        refreshView();
        return this;
    }

    public UiDataTable<T> applySorter(Comparator<T> sorter) {
        this.sorter = sorter;
        refreshView();
        return this;
    }

    public UiDataTable<T> clearFilterAndSort() {
        this.filter = item -> true;
        this.sorter = null;
        refreshView();
        return this;
    }

    public T getSelectedItem() {
        return table.getSelectionModel().getSelectedItem();
    }

    public TableView<T> table() {
        return table;
    }

    public List<T> getAllItemsSnapshot() {
        return new ArrayList<>(allItems);
    }

    private void refreshView() {
        // Yêu cầu đề bài: filter/sort bằng Streams API.
        List<T> result = allItems.stream()
                .filter(filter)
                .collect(Collectors.toList());

        if (sorter != null) {
            result = result.stream().sorted(sorter).collect(Collectors.toList());
        }

        table.setItems(FXCollections.observableArrayList(result));
    }
}
