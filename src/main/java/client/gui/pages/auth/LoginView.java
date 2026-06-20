package main.java.client.gui.pages.auth;

import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.field.UiField;
import main.java.client.gui.core.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.function.BiConsumer;

/**
 * Login screen view.
 * Contains: username field, password field, login button, link to register.
 */
public class LoginView extends StackPane {

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final TextField passwordVisible = new TextField();

    private final Label usernameError = new Label();
    private final Label passwordError = new Label();
    private final Label message = new Label();

    private BiConsumer<String, String> onLogin = (u, p) -> {};
    private Runnable onSwitchToRegister = () -> {};

    public LoginView() {
        getStyleClass().add("login-view");

        // Card with title/subtitle from i18n
        UiCard card = new UiCard(
                Messages.get(Messages.Key.LOGIN_TITLE),
                Messages.get(Messages.Key.LOGIN_SUBTITLE)
        );

        // Input placeholders from i18n
        username.setPromptText(Messages.get(Messages.Key.USERNAME));
        password.setPromptText(Messages.get(Messages.Key.PASSWORD));
        passwordVisible.setPromptText(Messages.get(Messages.Key.PASSWORD));
        password.getStyleClass().add("ui-input");
        passwordVisible.getStyleClass().add("ui-input");

        // Login button
        UiButton loginBtn = new UiButton(
                Messages.get(Messages.Key.LOGIN),
                ButtonVariant.DEFAULT
        );
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> handleLogin());

        Hyperlink switchLink = new Hyperlink(Messages.get(Messages.Key.SWITCH_TO_REGISTER));
        HBox linkContainer = new HBox(switchLink);
        linkContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(linkContainer, new Insets(8, 0, 0, 0));

        // Switch to register link
//        switchLink.setMaxWidth(Double.MAX_VALUE);
        switchLink.setMaxWidth(Hyperlink.USE_PREF_SIZE);
        switchLink.setStyle("-fx-alignment: center");
        switchLink.setOnAction(e -> onSwitchToRegister.run());

        // Error label styles
        usernameError.getStyleClass().add("field-error");
        passwordError.getStyleClass().add("field-error");
        message.getStyleClass().add("form-message");

//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//
//        HBox linkBox = new HBox(spacer, switchLink);
//        linkBox.setAlignment(Pos.CENTER_RIGHT);

        // Form layout
        VBox form = new VBox(8,
                new UiField(Messages.get(Messages.Key.USERNAME), username),
                usernameError,
                new UiField(Messages.get(Messages.Key.PASSWORD), buildPasswordToggleField(password, passwordVisible)),
                passwordError,
                message,
                loginBtn,
                linkContainer
        );
        form.setAlignment(Pos.CENTER_LEFT);

        card.content().getChildren().add(form);
        getChildren().add(card);
    }

    // Validate and trigger login
    private void handleLogin() {
        clearErrors();
        boolean valid = true;

        if (username.getText().isBlank()) {
            usernameError.setText(Messages.get(Messages.Key.ERROR_USERNAME_EMPTY));
            valid = false;
        }
        if (password.getText().isBlank()) {
            passwordError.setText(Messages.get(Messages.Key.ERROR_PASSWORD_EMPTY));
            valid = false;
        }

        if (valid) {
            onLogin.accept(username.getText(), password.getText());
        }
    }

    private void clearErrors() {
        usernameError.setText("");
        passwordError.setText("");
        message.setText("");
    }

    public void setOnLogin(BiConsumer<String, String> onLogin) {
        this.onLogin = onLogin == null ? (u, p) -> {} : onLogin;
    }

    public void setOnSwitchToRegister(Runnable onSwitch) {
        this.onSwitchToRegister = onSwitch == null ? () -> {} : onSwitch;
    }

    public void showMessage(String text) {
        message.setText(text == null ? "" : text);
    }

    public void showServerError(String text) {
        message.setText(text);
    }

    // переключатель видимости пароля: StackPane со скрытым и открытым полем + кнопка
    private javafx.scene.Node buildPasswordToggleField(PasswordField hidden, TextField visible) {
        // держим текст обоих полей синхронизированным
        hidden.textProperty().bindBidirectional(visible.textProperty());
        visible.setVisible(false);
        visible.setManaged(false);

        StackPane stack = new StackPane(hidden, visible);
        HBox.setHgrow(stack, Priority.ALWAYS);

        UiButton toggle = new UiButton("Показать", ButtonVariant.GHOST);
        toggle.setMinWidth(Region.USE_PREF_SIZE);
        toggle.setOnAction(e -> {
            boolean show = !visible.isVisible();
            hidden.setVisible(!show);
            hidden.setManaged(!show);
            visible.setVisible(show);
            visible.setManaged(show);
            toggle.setText(show ? "Скрыть" : "Показать");
            // фокус переводим на то поле, которое сейчас активно
            (show ? visible : hidden).requestFocus();
        });

        HBox box = new HBox(6, stack, toggle);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}
