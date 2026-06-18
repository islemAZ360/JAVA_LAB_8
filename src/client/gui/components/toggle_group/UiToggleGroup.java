package client.gui.components.toggle_group;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.function.Consumer;

/**
 * UiToggleGroup — exclusive toggle button group (shadcn ToggleGroup).
 *
 * Usage:
 *   UiToggleGroup<String> tg = new UiToggleGroup<>(List.of("Day", "Week", "Month"));
 *   tg.setOnChange(v -> loadData(v));
 */
public class UiToggleGroup<T> extends HBox {
    private final ToggleGroup group = new ToggleGroup();
    private Consumer<T> onChange;

    public UiToggleGroup(List<T> items) {
        getStyleClass().add("ui-toggle-group");
        setSpacing(2);
        for (T item : items) {
            ToggleButton btn = new ToggleButton(item.toString());
            btn.getStyleClass().add("ui-toggle-group-item");
            btn.setToggleGroup(group);
            btn.setUserData(item);
            getChildren().add(btn);
        }
        group.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n != null && onChange != null) {
                @SuppressWarnings("unchecked") T v = (T) n.getUserData();
                onChange.accept(v);
            }
        });
    }

    public void setOnChange(Consumer<T> cb) { this.onChange = cb; }

    @SuppressWarnings("unchecked")
    public T selected() {
        var t = group.getSelectedToggle();
        return t == null ? null : (T) t.getUserData();
    }

    public void selectFirst() {
        if (!getChildren().isEmpty() && getChildren().get(0) instanceof ToggleButton btn)
            btn.setSelected(true);
    }
}
