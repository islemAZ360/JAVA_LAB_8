package client.gui.components.button;

import javafx.scene.control.Button;

import java.util.Arrays;

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
}
