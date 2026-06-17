package client.ui.template.mockup;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.components.card.UiCard;
import client.ui.template.components.field.UiField;
import client.ui.template.core.Messages;

import java.util.function.BiConsumer;

public class LoginView extends StackPane {
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label message = new Label();
    private BiConsumer<String, String> onLogin = (u, p) -> {};
    private BiConsumer<String, String> onRegister = (u, p) -> {};

    public LoginView() {
        getStyleClass().add("login-view");

        UiCard card = new UiCard(Messages.get(Messages.Key.LOGIN_TITLE), "HumanBeing Collection Client");
//        Multi css class
//        card.getTitleLabel().getStyleClass().addAll("login-view", "ui-card-title");
//        CSS replace
//        card.setMaxWidth(420);
//        card.setMaxHeight(500);

        username.setPromptText(Messages.get(Messages.Key.USERNAME));
        password.setPromptText(Messages.get(Messages.Key.PASSWORD));

        UiButton login = new UiButton(Messages.get(Messages.Key.LOGIN), ButtonVariant.DEFAULT);
        UiButton register = new UiButton(Messages.get(Messages.Key.REGISTER), ButtonVariant.OUTLINE);

        login.setOnAction(e -> onLogin.accept(username.getText(), password.getText()));
        register.setOnAction(e -> onRegister.accept(username.getText(), password.getText()));

        HBox actions = new HBox(8, login, register);
        actions.setAlignment(Pos.CENTER_RIGHT);
        message.getStyleClass().add("form-message");

        card.content().getChildren().addAll(
                new UiField(Messages.get(Messages.Key.USERNAME), username),
                new UiField(Messages.get(Messages.Key.PASSWORD), password),
                message
        );
        card.footer().getChildren().add(actions);
        getChildren().add(card);
    }

    public void setOnLogin(BiConsumer<String, String> onLogin) {
        this.onLogin = onLogin == null ? (u, p) -> {} : onLogin;
    }

    public void setOnRegister(BiConsumer<String, String> onRegister) {
        this.onRegister = onRegister == null ? (u, p) -> {} : onRegister;
    }

    public void showMessage(String text) {
        message.setText(text == null ? "" : text);
    }
}
