package client.gui.components.radio_group;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * UiRadioGroup — group of radio buttons (shadcn RadioGroup).
 *
 * Usage:
 *   UiRadioGroup<String> rg = new UiRadioGroup<>(List.of("Option A", "Option B", "Option C"));
 *   rg.setOnChange(v -> System.out.println("Selected: " + v));
 *   String val = rg.selected();
 */
public class UiRadioGroup<T> extends VBox {
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final List<T> values = new ArrayList<>();
    private Consumer<T> onChange;

    public UiRadioGroup(List<T> items) {
        getStyleClass().add("ui-radio-group");
        setSpacing(8);
        for (T item : items) {
            RadioButton rb = new RadioButton(item.toString());
            rb.getStyleClass().add("ui-radio");
            rb.setToggleGroup(toggleGroup);
            rb.setUserData(item);
            values.add(item);
            getChildren().add(rb);
        }
        toggleGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n != null && onChange != null) {
                @SuppressWarnings("unchecked") T val = (T) n.getUserData();
                onChange.accept(val);
            }
        });
    }

    public void setOnChange(Consumer<T> cb) { this.onChange = cb; }

    @SuppressWarnings("unchecked")
    public T selected() {
        var toggle = toggleGroup.getSelectedToggle();
        return toggle == null ? null : (T) toggle.getUserData();
    }

    public void selectFirst() {
        if (!getChildren().isEmpty() && getChildren().get(0) instanceof RadioButton rb)
            rb.setSelected(true);
    }
}
