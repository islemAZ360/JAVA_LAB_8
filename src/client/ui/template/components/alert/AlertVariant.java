package client.ui.template.components.alert;

public enum AlertVariant {
    INFO("ui-alert-info"),
    SUCCESS("ui-alert-success"),
    WARNING("ui-alert-warning"),
    ERROR("ui-alert-error");

    private final String cssClass;

    AlertVariant(String cssClass) {
        this.cssClass = cssClass;
    }

    public String cssClass() {
        return cssClass;
    }
}
