package main.java.client.gui.mockup.dashboard;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.core.Messages;
import main.java.client.gui.core.Theme;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class DashboardTopBar extends HBox {
    private final String currentUser;
    private final Runnable onThemeSwitch;
    private final Runnable onLanguageChanged;
    private final Runnable onLogout;

    private final Label logo = new Label("HumanBeing GUI");
    private final Label currentUserLabel = new Label();
    private final UiButton langButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton themeButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton logoutButton = new UiButton("", ButtonVariant.DESTRUCTIVE);

    private Theme currentTheme = Theme.DARK;

    public DashboardTopBar(String currentUser, Runnable onThemeSwitch, Runnable onLanguageChanged, Runnable onLogout) {
        this.currentUser = currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
        this.onThemeSwitch = onThemeSwitch;
        this.onLanguageChanged = onLanguageChanged;
        this.onLogout = onLogout;

        getStyleClass().add("top-bar");
        setAlignment(Pos.CENTER_LEFT);

        logo.getStyleClass().add("app-logo");
        currentUserLabel.getStyleClass().add("current-user-label");

        langButton.setOnAction(e -> {
            Messages.setLang(Messages.nextLang());
            if (this.onLanguageChanged != null) {
                this.onLanguageChanged.run();
            }
        });

        themeButton.setOnAction(e -> {
            if (this.onThemeSwitch != null) {
                this.onThemeSwitch.run();
            }
        });

        logoutButton.setOnAction(e -> {
            if (this.onLogout != null) {
                this.onLogout.run();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(logo, spacer, currentUserLabel, langButton, themeButton, logoutButton);

        refreshLanguage();
    }

    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme == null ? Theme.DARK : currentTheme;
        refreshLanguage();
    }

    public void refreshLanguage() {
        currentUserLabel.setText(Messages.get(Messages.Key.CURRENT_USER) + ": " + currentUser);
        langButton.setText(Messages.getCurrentLang().displayName());
        themeButton.setText(currentTheme == Theme.DARK
                ? Messages.get(Messages.Key.THEME_DARK)
                : Messages.get(Messages.Key.THEME_LIGHT));
        logoutButton.setText(Messages.get(Messages.Key.LOGOUT));
    }
}
