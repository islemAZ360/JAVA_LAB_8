package client.gui.core;

public enum Theme {
    DARK("theme-dark"),
    LIGHT("theme-light"),
    CYBERPUNK("theme-cyberpunk");

    private final String cssClass;

    Theme(String cssClass) {
        this.cssClass = cssClass;
    }

    public String cssClass() {
        return cssClass;
    }
}
