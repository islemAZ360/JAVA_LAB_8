package main.java.client.gui.pages.dashboard;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.field.UiField;
import main.java.client.gui.components.select.UiSelect;
import main.java.client.gui.core.Direction;
import main.java.client.gui.core.LangEventBus;
import main.java.client.gui.core.Messages;
import main.java.client.gui.core.Theme;
import main.java.client.gui.layout.BasePage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * Page 5: Theme / RTL / i18n settings.
 * Allows switching theme, direction, and language.
 */
public class SettingsPage extends BasePage {

    private Theme currentTheme = Theme.DARK;
    private Direction currentDirection = Direction.LTR;

    private Consumer<Theme> onThemeChange;
    private Consumer<Direction> onDirectionChange;

    public SettingsPage() {
        super(
                Messages.get(Messages.Key.PAGE_SETTINGS_TITLE),
                Messages.get(Messages.Key.PAGE_SETTINGS_DESCRIPTION)
        );

        buildContent();
    }

    private void buildContent() {
        // ========================================
        // THEME CARD
        // ========================================
        UiSelect<Theme> themeSelect = new UiSelect<>(
                List.of(Theme.DARK, Theme.LIGHT, Theme.CYBERPUNK)
        );
        themeSelect.getSelectionModel().select(currentTheme);

        UiButton applyTheme = new UiButton(
                Messages.get(Messages.Key.SETTINGS_APPLY_THEME),
                ButtonVariant.DEFAULT
        );
        applyTheme.setOnAction(e -> {
            Theme selectedTheme = themeSelect.selected();
            if (selectedTheme != null) {
                this.currentTheme = selectedTheme;
                if (onThemeChange != null) {
                    onThemeChange.accept(selectedTheme);
                }
            }
        });

        UiCard themeCard = new UiCard(
                Messages.get(Messages.Key.SETTINGS_THEME_TITLE),
                Messages.get(Messages.Key.SETTINGS_THEME_DESC)
        );
        themeCard.content().getChildren().addAll(
                new UiField(Messages.get(Messages.Key.SETTINGS_THEME_LABEL), themeSelect),
                applyTheme
        );

        // ========================================
        // DIRECTION CARD
        // ========================================
        UiSelect<Direction> directionSelect = new UiSelect<>(
                List.of(Direction.LTR, Direction.RTL)
        );
        directionSelect.getSelectionModel().select(currentDirection);

        UiButton applyDirection = new UiButton(
                Messages.get(Messages.Key.SETTINGS_APPLY_DIRECTION),
                ButtonVariant.DEFAULT
        );
        applyDirection.setOnAction(e -> {
            Direction selectedDirection = directionSelect.selected();
            if (selectedDirection != null) {
                this.currentDirection = selectedDirection;
                if (onDirectionChange != null) {
                    onDirectionChange.accept(selectedDirection);
                }
            }
        });

        UiCard directionCard = new UiCard(
                Messages.get(Messages.Key.SETTINGS_DIRECTION_TITLE),
                Messages.get(Messages.Key.SETTINGS_DIRECTION_DESC)
        );
        directionCard.content().getChildren().addAll(
                new UiField(Messages.get(Messages.Key.SETTINGS_DIRECTION_LABEL), directionSelect),
                applyDirection
        );

        // ========================================
        // LANGUAGE CARD
        // ========================================
        UiSelect<Messages.Lang> langSelect = new UiSelect<>(
                List.of(Messages.Lang.RU, Messages.Lang.EN_CA, Messages.Lang.SK, Messages.Lang.SQ)
        );
        langSelect.getSelectionModel().select(Messages.getCurrentLang());

        Label langPreview = new Label(buildLangPreview());
        langPreview.setWrapText(true);

        UiButton applyLang = new UiButton(
                Messages.get(Messages.Key.SETTINGS_APPLY_LANG),
                ButtonVariant.DEFAULT
        );
        applyLang.setOnAction(e -> {
            Messages.Lang selectedLang = langSelect.selected();
            if (selectedLang != null) {
                // через шину — чтобы подписчики (MainShell) тоже обновили язык
                LangEventBus.setLang(selectedLang);
                langPreview.setText(buildLangPreview());
            }
        });

        UiCard langCard = new UiCard(
                Messages.get(Messages.Key.SETTINGS_LANG_TITLE),
                Messages.get(Messages.Key.SETTINGS_LANG_DESC)
        );
        langCard.content().getChildren().addAll(
                new UiField(Messages.get(Messages.Key.SETTINGS_LANG_LABEL), langSelect),
                applyLang,
                langPreview
        );

        VBox cardWrapper = new VBox(8);
        cardWrapper.getChildren().addAll(themeCard, directionCard, langCard);

        this.getChildren().addAll(wrapInScroll(cardWrapper));
    }

    // ========================================
    // CALLBACKS
    // ========================================

    public void setOnThemeChange(Consumer<Theme> onThemeChange) {
        this.onThemeChange = onThemeChange;
    }

    public void setOnDirectionChange(Consumer<Direction> onDirectionChange) {
        this.onDirectionChange = onDirectionChange;
    }

    // ========================================
    // HELPERS
    // ========================================

    private String buildLangPreview() {
        return Messages.get(Messages.Key.LOGIN_TITLE) + " | "
                + Messages.get(Messages.Key.ADD) + " | "
                + Messages.get(Messages.Key.EDIT) + " | "
                + Messages.get(Messages.Key.DELETE) + " | "
                + Messages.get(Messages.Key.LOGOUT);
    }
}
