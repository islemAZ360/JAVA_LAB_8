package client.gui.components.combobox;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * UiCombobox — searchable/filterable combobox (shadcn Combobox).
 * Extends ComboBox with live-search filtering.
 *
 * Usage:
 *   UiCombobox<String> cb = new UiCombobox<>(List.of("Apple","Banana","Cherry"));
 *   cb.setOnSelect(v -> System.out.println("Chosen: " + v));
 */
public class UiCombobox<T> extends ComboBox<T> {
    private final List<T> allItems;
    private Consumer<T> onSelect;

    public UiCombobox(Collection<T> items) {
        getStyleClass().add("ui-combobox");
        setEditable(true);
        setMaxWidth(Double.MAX_VALUE);
        this.allItems = List.copyOf(items);
        setItems(FXCollections.observableArrayList(allItems));

        // Live filter on editor input
        TextField editor = getEditor();
        editor.textProperty().addListener((obs, o, query) -> {
            if (query == null || query.isBlank()) {
                setItems(FXCollections.observableArrayList(allItems));
            } else {
                String q = query.toLowerCase();
                List<T> filtered = allItems.stream()
                    .filter(item -> item.toString().toLowerCase().contains(q))
                    .collect(Collectors.toList());
                setItems(FXCollections.observableArrayList(filtered));
                if (!filtered.isEmpty()) show();
            }
        });

        getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null && onSelect != null) onSelect.accept(n);
        });
    }

    public void setOnSelect(Consumer<T> cb) { this.onSelect = cb; }
    public T selected() { return getSelectionModel().getSelectedItem(); }

    public void reset() {
        getSelectionModel().clearSelection();
        getEditor().clear();
        setItems(FXCollections.observableArrayList(allItems));
    }
}
