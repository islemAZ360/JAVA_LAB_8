package main.java.client.gui.components.table;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * UiTable — simple non-filterable table (shadcn Table).
 * Use UiDataTable when you need filter/sort/streams.
 *
 * Usage:
 *   UiTable<Person> t = new UiTable<>();
 *   t.addColumn("Name", Person::getName, 160);
 *   t.addColumn("Age",  p -> String.valueOf(p.getAge()), 60);
 *   t.setItems(List.of(alice, bob));
 */
public class UiTable<T> extends TableView<T> {
    public UiTable() {
        getStyleClass().add("ui-data-table");
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setPlaceholder(new Label("No data"));
    }

    public <V> UiTable<T> addColumn(String title, Function<T, V> accessor, double width) {
        TableColumn<T, V> col = new TableColumn<>(title);
        col.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(accessor.apply(d.getValue())));
        if (width > 0) col.setPrefWidth(width);
        getColumns().add(col);
        return this;
    }

    public UiTable<T> setItems(Collection<T> items) {
        super.setItems(FXCollections.observableArrayList(items == null ? List.of() : items));
        return this;
    }

    public T getSelectedItem() { return getSelectionModel().getSelectedItem(); }
}
