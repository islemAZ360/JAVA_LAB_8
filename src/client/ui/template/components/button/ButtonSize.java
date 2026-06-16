package client.ui.template.components.button;

public enum ButtonSize {
    SMALL("ui-button-sm"),
    DEFAULT("ui-button-md"),
    LARGE("ui-button-lg"),
    ICON("ui-button-icon");

    private final String cssClass;

    ButtonSize(String cssClass) {
        this.cssClass = cssClass;
    }

    public String cssClass() {
        return cssClass;
    }
}
