package main.java.client.gui.components.native_select;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.util.Collection;

/**
 * UiSelect — styled ComboBox
 *
 * Usage:
 *   UiSelect<Theme> sel = new UiSelect<>(List.of(Theme.DARK, Theme.LIGHT));
 *   sel.selectFirstIfAny();
 *   Theme chosen = sel.selected();
 */
public class UiNativeSelect<T> extends ComboBox<T> {

    public UiNativeSelect() {
        getStyleClass().add("ui-select");
        setMaxWidth(Double.MAX_VALUE);
    }

    public UiNativeSelect(Collection<T> items) {
        this();
        if (items != null) {
            setItems(FXCollections.observableArrayList(items));
        }
    }

    // Sửa kiểu trả về từ UiSelect<T> thành UiNativeSelect<T>
    public UiNativeSelect<T> selectFirstIfAny() {
        if (!getItems().isEmpty()) {
            getSelectionModel().select(0);
        }
        return this;
    }

    // Sửa kiểu trả về từ UiSelect<T> thành UiNativeSelect<T>
    public UiNativeSelect<T> selectItem(T item) {
        getSelectionModel().select(item);
        return this;
    }

    public T selected() {
        return getSelectionModel().getSelectedItem();
    }
}
