package main.java.client.gui.pages.dashboard.prototype;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.core.LangEventBus;
import main.java.client.gui.core.Messages;
import main.java.client.gui.core.Theme;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class DashboardTopBar extends HBox {

    private final String currentUser;
    private final Runnable onThemeSwitch;
    private final Runnable onLanguageChanged;
    private final Runnable onLogout;

    private final Label logo = new Label();
    private final Label currentUserLabel = new Label();
    private final UiButton langButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton themeButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton logoutButton = new UiButton("", ButtonVariant.DESTRUCTIVE);

    private Theme currentTheme = Theme.DARK;

    // Dropdown menu chọn ngôn ngữ
    private final ContextMenu langMenu = new ContextMenu();

    public DashboardTopBar(String currentUser,
                           Runnable onThemeSwitch,
                           Runnable onLanguageChanged,
                           Runnable onLogout) {
        this.currentUser = currentUser == null || currentUser.isBlank() ? "demo_user" : currentUser;
        this.onThemeSwitch = onThemeSwitch;
        this.onLanguageChanged = onLanguageChanged;
        this.onLogout = onLogout;

        getStyleClass().add("top-bar");
        setAlignment(Pos.CENTER_LEFT);

        logo.getStyleClass().add("app-logo");
        currentUserLabel.getStyleClass().add("current-user-label");

        // Xây dựng dropdown menu với tất cả 4 ngôn ngữ
        buildLangMenu();

        // Click vào langButton → hiện dropdown ngay dưới button
        langButton.setOnAction(e -> {
            langMenu.show(langButton,
                    langButton.localToScreen(0, langButton.getHeight()).getX(),
                    langButton.localToScreen(0, langButton.getHeight()).getY());
        });

        themeButton.setOnAction(e -> {
            if (this.onThemeSwitch != null) this.onThemeSwitch.run();
        });

        logoutButton.setOnAction(e -> {
            if (this.onLogout != null) this.onLogout.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(logo, spacer, currentUserLabel, langButton, themeButton, logoutButton);

        refreshLanguage();
    }

    private void buildLangMenu() {
        langMenu.getItems().clear();
        for (Messages.Lang lang : Messages.Lang.values()) {
            MenuItem item = new MenuItem(lang.displayName());
            // Highlight ngôn ngữ đang chọn
            if (lang == Messages.getCurrentLang()) {
                item.setStyle("-fx-font-weight: bold;");
            }
            item.setOnAction(e -> {
                LangEventBus.setLang(lang);
                buildLangMenu(); // rebuild để cập nhật bold
                if (this.onLanguageChanged != null) this.onLanguageChanged.run();
            });
            langMenu.getItems().add(item);
        }
    }

    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme == null ? Theme.DARK : currentTheme;
        refreshLanguage();
    }

    public void refreshLanguage() {
        logo.setText(Messages.get(Messages.Key.APP_LOGO));
        currentUserLabel.setText(Messages.get(Messages.Key.CURRENT_USER) + ": " + currentUser);

        // Hiển thị tên ngôn ngữ hiện tại trên button + icon ▼
        langButton.setText("🌐 " + Messages.getCurrentLang().displayName() + " ▾");

        themeButton.setText(currentTheme == Theme.DARK
                ? Messages.get(Messages.Key.THEME_BUTTON_DARK)
                : Messages.get(Messages.Key.THEME_BUTTON_LIGHT));

        logoutButton.setText(Messages.get(Messages.Key.LOGOUT));

        // Rebuild menu để cập nhật bold (ngôn ngữ đang chọn)
        buildLangMenu();
    }
}