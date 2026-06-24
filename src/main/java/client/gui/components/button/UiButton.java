package main.java.client.gui.components.button;

import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public class UiButton extends Button {
    private ButtonVariant variant = ButtonVariant.DEFAULT;
    private ButtonSize size = ButtonSize.DEFAULT;

    public UiButton(String text) {
        super(text);
        getStyleClass().add("ui-button");
        applyVariant(ButtonVariant.DEFAULT);
        applySize(ButtonSize.DEFAULT);
    }

    public UiButton(String text, ButtonVariant variant) {
        this(text);
        applyVariant(variant);
    }

    public UiButton applyVariant(ButtonVariant newVariant) {
        getStyleClass().removeAll(Arrays.stream(ButtonVariant.values()).map(ButtonVariant::cssClass).toList());
        this.variant = newVariant == null ? ButtonVariant.DEFAULT : newVariant;
        getStyleClass().add(this.variant.cssClass());
        return this;
    }

    public UiButton applySize(ButtonSize newSize) {
        getStyleClass().removeAll(Arrays.stream(ButtonSize.values()).map(ButtonSize::cssClass).toList());
        this.size = newSize == null ? ButtonSize.DEFAULT : newSize;
        getStyleClass().add(this.size.cssClass());
        return this;
    }

    public ButtonVariant getVariant() {
        return variant;
    }

    public ButtonSize getButtonSize() {
        return size;
    }

    /**
     * Upgrades the button to a Dynamic Submit Button.
     * Fetches data via the provided Supplier and fires a ComponentSubmitEvent to the parent container.
     *
     * @param dataSupplier Lambda to retrieve current component values
     * @return This button instance for fluent chaining
     */
    public UiButton setupDynamicSubmit(Supplier<Map<String, Object>> dataSupplier) {
        this.setOnAction(e -> {
            if (dataSupplier != null) {
                // Retrieve raw data from external components
                // 1. Invoke the lambda to collect raw data (e.g., rg.selected(), slider.getValue())
                Map<String, Object> data = dataSupplier.get();

                // Dispatch the event up the JavaFX hierarchy
                // 2. Fire the custom event up the JavaFX Node Tree
                this.fireEvent(new ComponentSubmitEvent(data));
            }
        });
        return this; // Enables Fluent API chaining
    }

}
