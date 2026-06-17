package client.ui.template.mockup.dashboard;

import client.ui.template.components.button.ButtonVariant;
import client.ui.template.components.button.UiButton;
import client.ui.template.core.Messages;
import client.ui.template.model.HumanBeingUiModel;
import javafx.beans.binding.ObjectExpression;
import javafx.scene.layout.HBox;

import java.util.List;

public class HumanBeingCommandBar extends HBox {
    private final UiButton addButton = new UiButton("");
    private final UiButton editButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton deleteButton = new UiButton("", ButtonVariant.DESTRUCTIVE);
    private final UiButton clearMineButton = new UiButton("", ButtonVariant.OUTLINE);
    private final UiButton infoButton = new UiButton("", ButtonVariant.GHOST);
    private final UiButton addIfMaxButton = new UiButton("", ButtonVariant.GHOST);
    private final UiButton addIfMinButton = new UiButton("", ButtonVariant.GHOST);
    private final UiButton removeGreaterButton = new UiButton("", ButtonVariant.GHOST);
    private final UiButton refreshButton = new UiButton("", ButtonVariant.SECONDARY);

    public HumanBeingCommandBar() {
        getStyleClass().add("command-bar");
        setSpacing(8);

        getChildren().addAll(
                addButton,
                editButton,
                deleteButton,
                clearMineButton,
                infoButton,
                addIfMaxButton,
                addIfMinButton,
                removeGreaterButton,
                refreshButton
        );

        refreshLanguage();
    }

    public void bindSelection(ObjectExpression<HumanBeingUiModel> selectedItemProperty) {
        List<UiButton> needsSelection = List.of(editButton, deleteButton, infoButton, removeGreaterButton);
        needsSelection.forEach(button -> button.disableProperty().bind(selectedItemProperty.isNull()));
    }

    public void refreshLanguage() {
        addButton.setText(Messages.get(Messages.Key.ADD));
        editButton.setText(Messages.get(Messages.Key.EDIT));
        deleteButton.setText(Messages.get(Messages.Key.DELETE));
        clearMineButton.setText(Messages.get(Messages.Key.CLEAR_MINE));
        infoButton.setText(Messages.get(Messages.Key.INFO));
        addIfMaxButton.setText(Messages.get(Messages.Key.ADD_IF_MAX));
        addIfMinButton.setText(Messages.get(Messages.Key.ADD_IF_MIN));
        removeGreaterButton.setText(Messages.get(Messages.Key.REMOVE_GREATER));
        refreshButton.setText(Messages.get(Messages.Key.REFRESH));
    }

    public void setOnAdd(Runnable action) {
        addButton.setOnAction(e -> run(action));
    }

    public void setOnEdit(Runnable action) {
        editButton.setOnAction(e -> run(action));
    }

    public void setOnDelete(Runnable action) {
        deleteButton.setOnAction(e -> run(action));
    }

    public void setOnClearMine(Runnable action) {
        clearMineButton.setOnAction(e -> run(action));
    }

    public void setOnInfo(Runnable action) {
        infoButton.setOnAction(e -> run(action));
    }

    public void setOnAddIfMax(Runnable action) {
        addIfMaxButton.setOnAction(e -> run(action));
    }

    public void setOnAddIfMin(Runnable action) {
        addIfMinButton.setOnAction(e -> run(action));
    }

    public void setOnRemoveGreater(Runnable action) {
        removeGreaterButton.setOnAction(e -> run(action));
    }

    public void setOnRefresh(Runnable action) {
        refreshButton.setOnAction(e -> run(action));
    }

    private void run(Runnable action) {
        if (action != null) {
            action.run();
        }
    }
}
