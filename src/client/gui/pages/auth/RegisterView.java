package client.gui.pages.auth;

import client.gui.components.button.ButtonVariant;
import client.gui.components.button.UiButton;
import client.gui.components.card.UiCard;
import client.gui.components.field.UiField;
import client.gui.core.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

/**
 * Register screen view.
 * Contains: username, password, confirm password, register button, link to login.
 */
public class RegisterView extends StackPane {

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField confirmPassword = new PasswordField();

    private final Label usernameError = new Label();
    private final Label passwordError = new Label();
    private final Label confirmError = new Label();
    private final Label message = new Label();

    private BiConsumer<String, String> onRegister = (u, p) -> {};
    private Runnable onSwitchToLogin = () -> {};

    public RegisterView() {
        getStyleClass().add("login-view");

        // Card with i18n title/subtitle
        UiCard card = new UiCard(
                Messages.get(Messages.Key.REGISTER_TITLE),
                Messages.get(Messages.Key.REGISTER_SUBTITLE)
        );

        // Input placeholders from i18n
        username.setPromptText(Messages.get(Messages.Key.USERNAME));
        password.setPromptText(Messages.get(Messages.Key.PASSWORD));
        confirmPassword.setPromptText(Messages.get(Messages.Key.CONFIRM_PASSWORD));

        // Register button
        UiButton registerBtn = new UiButton(
                Messages.get(Messages.Key.REGISTER),
                ButtonVariant.DEFAULT
        );
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e -> handleRegister());

        // Switch to login link
        Hyperlink switchLink = new Hyperlink(Messages.get(Messages.Key.SWITCH_TO_LOGIN));
        switchLink.setMaxWidth(Hyperlink.USE_PREF_SIZE);
        switchLink.setMaxHeight(Hyperlink.USE_PREF_SIZE);

        HBox linkContainer = new HBox(switchLink);
        linkContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(linkContainer, new Insets(8, 0, 0, 0));

        switchLink.setStyle("-fx-alignment: center");
        switchLink.setOnAction(e -> onSwitchToLogin.run());

        // Error label styles
        usernameError.getStyleClass().add("field-error");
        passwordError.getStyleClass().add("field-error");
        confirmError.getStyleClass().add("field-error");
        message.getStyleClass().add("form-message");

        // Form layout
        VBox form = new VBox(8,
                new UiField(Messages.get(Messages.Key.USERNAME), username),
                usernameError,
                new UiField(Messages.get(Messages.Key.PASSWORD), password),
                passwordError,
                new UiField(Messages.get(Messages.Key.CONFIRM_PASSWORD), confirmPassword),
                confirmError,
                message,
                registerBtn,
                linkContainer
        );
        form.setAlignment(Pos.CENTER_LEFT);

        card.content().getChildren().add(form);
        getChildren().add(card);
    }

    // Validate and trigger register
    private void handleRegister() {
        clearErrors();
        boolean valid = true;

        String u = username.getText().trim();
        String p = password.getText();
        String cp = confirmPassword.getText();

        // Username validation
        if (u.isEmpty()) {
            usernameError.setText(Messages.get(Messages.Key.ERROR_USERNAME_EMPTY));
            valid = false;
        } else if (u.length() < 3) {
            usernameError.setText(Messages.get(Messages.Key.ERROR_USERNAME_TOO_SHORT));
            valid = false;
        }

        // Password validation
        if (p.isEmpty()) {
            passwordError.setText(Messages.get(Messages.Key.ERROR_PASSWORD_EMPTY));
            valid = false;
        } else if (p.length() < 6) {
            passwordError.setText(Messages.get(Messages.Key.ERROR_PASSWORD_TOO_SHORT));
            valid = false;
        }

        // Confirm password validation
        if (cp.isEmpty()) {
            confirmError.setText(Messages.get(Messages.Key.ERROR_CONFIRM_PASSWORD_EMPTY));
            valid = false;
        } else if (!p.equals(cp)) {
            confirmError.setText(Messages.get(Messages.Key.ERROR_PASSWORD_MISMATCH));
            valid = false;
        }

        if (valid) {
            onRegister.accept(u, p);
        }
    }

    private void clearErrors() {
        usernameError.setText("");
        passwordError.setText("");
        confirmError.setText("");
        message.setText("");
    }

    public void setOnRegister(BiConsumer<String, String> onRegister) {
        this.onRegister = onRegister == null ? (u, p) -> {} : onRegister;
    }

    public void setOnSwitchToLogin(Runnable onSwitch) {
        this.onSwitchToLogin = onSwitch == null ? () -> {} : onSwitch;
    }

    public void showMessage(String text) {
        message.setText(text == null ? "" : text);
    }

    public void showServerError(String text) {
        message.setText(text);
    }
}
