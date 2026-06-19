package main.java.client.gui.components.badge;
public enum BadgeVariant {
    DEFAULT("ui-badge-default"), SECONDARY("ui-badge-secondary"),
    OUTLINE("ui-badge-outline"), DESTRUCTIVE("ui-badge-destructive"),
    SUCCESS("ui-badge-success"), WARNING("ui-badge-warning");
    private final String cssClass;
    BadgeVariant(String c) { cssClass = c; }
    public String cssClass() { return cssClass; }
}
