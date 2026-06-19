package main.java.client.gui.components.toast;
public enum ToastVariant {
    DEFAULT("ui-toast-default"), SUCCESS("ui-toast-success"),
    WARNING("ui-toast-warning"), ERROR("ui-toast-error"),
    INFO("ui-toast-info");
    private final String cssClass;
    ToastVariant(String c) { cssClass = c; }
    public String cssClass() { return cssClass; }
}
