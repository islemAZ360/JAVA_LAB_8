package client.gui.components.otp;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UiInputOtp extends HBox {
    private final List<TextField> fields = new ArrayList<>();
    private Consumer<String> onComplete;

    public UiInputOtp(int digits) {
        getStyleClass().add("ui-otp");
        setSpacing(8);
        setAlignment(Pos.CENTER);

        for (int i = 0; i < digits; i++) {
            TextField tf = new TextField();
            tf.getStyleClass().add("ui-otp-field");
            tf.setPrefWidth(44);
            tf.setPrefHeight(52);
            tf.setAlignment(Pos.CENTER);

            tf.setTextFormatter(new TextFormatter<>(change -> {
                if (change.getControlNewText().length() > 1) {
                    return null;
                }
                return change;
            }));

            int idx = i;

            tf.textProperty().addListener((obs, o, n) -> {
                if (n != null && !n.isBlank() && idx < fields.size() - 1) {
                    fields.get(idx + 1).requestFocus();
                }
                checkComplete();
            });

            tf.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.BACK_SPACE && tf.getText().isEmpty() && idx > 0) {
                    fields.get(idx - 1).requestFocus();
                    fields.get(idx - 1).clear();
                }
            });

            fields.add(tf);
            getChildren().add(tf);
        }
    }

    public UiInputOtp setOnComplete(Consumer<String> cb) {
        this.onComplete = cb;
        return this;
    }

    public String getValue() {
        StringBuilder sb = new StringBuilder();
        fields.forEach(f -> sb.append(f.getText().trim()));
        return sb.toString();
    }

    public void clear() {
        fields.forEach(TextField::clear);
        if (!fields.isEmpty()) {
            fields.get(0).requestFocus();
        }
    }

    private void checkComplete() {
        String v = getValue();
        if (v.length() == fields.size() && onComplete != null) {
            onComplete.accept(v);
        }
    }
}
