package client.ui.template.components.avatar;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class UiAvatar extends StackPane {
    private final Circle circle = new Circle(18);
    private final Label initials = new Label();

    public UiAvatar(String name) {
        getStyleClass().add("ui-avatar");
        setAlignment(Pos.CENTER);
        circle.getStyleClass().add("ui-avatar-circle");
        initials.getStyleClass().add("ui-avatar-initials");
        setName(name);
        getChildren().addAll(circle, initials);
    }

    public UiAvatar setName(String name) {
        initials.setText(makeInitials(name));
        return this;
    }

    public UiAvatar setRadius(double radius) {
        circle.setRadius(radius);
        return this;
    }

    private String makeInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}
