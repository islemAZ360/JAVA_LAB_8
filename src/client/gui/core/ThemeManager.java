package client.gui.core;

import javafx.scene.Parent;

import java.util.Arrays;

public final class ThemeManager {
    private ThemeManager() {}

    public static void applyTheme(Parent root, Theme theme) {
        root.getStyleClass().removeAll(Arrays.stream(Theme.values()).map(Theme::cssClass).toList());
        root.getStyleClass().add(theme.cssClass());
    }
}
