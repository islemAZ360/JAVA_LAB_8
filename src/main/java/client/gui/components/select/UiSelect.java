package main.java.client.gui.components.select;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.Collection;

public class UiSelect<T> extends ComboBox<T> {
    public UiSelect(Collection<T> items) {
        getStyleClass().add("ui-select");
        if (items != null) setItems(FXCollections.observableArrayList(items));
    }

    public T selected() {
        return getSelectionModel().getSelectedItem();
    }

    public UiSelect<T> selectFirstIfAny() {
        if (!getItems().isEmpty()) getSelectionModel().select(0);
        return this;
    }
}
