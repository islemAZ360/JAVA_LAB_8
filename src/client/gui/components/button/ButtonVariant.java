package client.gui.components.button;

public enum ButtonVariant {
    DEFAULT("ui-button-default"),
    SECONDARY("ui-button-secondary"),
    OUTLINE("ui-button-outline"),
    GHOST("ui-button-ghost"),
    DESTRUCTIVE("ui-button-destructive");

    private final String cssClass;

    ButtonVariant(String cssClass) {
        this.cssClass = cssClass;
    }

    public String cssClass() {
        return cssClass;
    }
}
