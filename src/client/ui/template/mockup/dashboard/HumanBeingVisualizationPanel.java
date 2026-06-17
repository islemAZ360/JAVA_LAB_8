package client.ui.template.mockup.dashboard;

import client.ui.template.components.card.UiCard;
import client.ui.template.components.empty.UiEmpty;
import client.ui.template.core.Messages;
import client.ui.template.model.HumanBeingUiModel;
import client.ui.template.mockup.ObjectVisualizationCanvas;
import javafx.scene.control.Label;

import java.util.Collection;
import java.util.function.Consumer;

public class HumanBeingVisualizationPanel extends UiCard {
    private final ObjectVisualizationCanvas canvas = new ObjectVisualizationCanvas();
    private final UiEmpty hint = new UiEmpty("", "");
    private final Label objectInfo = new Label();

    public HumanBeingVisualizationPanel() {
        canvas.setHeight(410);
        canvas.setWidth(620);

        objectInfo.setWrapText(true);
        objectInfo.getStyleClass().add("object-info");

        content().getChildren().addAll(canvas, hint, objectInfo);
        refreshLanguage();
    }

    public void setItems(Collection<HumanBeingUiModel> items) {
        canvas.setItems(items);
    }

    public void setSelectedObject(HumanBeingUiModel selected) {
        canvas.setSelectedObject(selected);
        showObjectInfo(selected);
    }

    public void setOnObjectSelected(Consumer<HumanBeingUiModel> onObjectSelected) {
        canvas.setOnObjectSelected(onObjectSelected);
    }

    public void refreshLanguage() {
        setTitle(Messages.get(Messages.Key.VISUALIZATION_TITLE));
        setDescription(Messages.get(Messages.Key.VISUALIZATION_DESCRIPTION));
        hint.setText(
                Messages.get(Messages.Key.CLICK_OBJECT),
                Messages.get(Messages.Key.CLICK_OBJECT_DESCRIPTION)
        );

        if (objectInfo.getText() == null || objectInfo.getText().isBlank()) {
            objectInfo.setText(Messages.get(Messages.Key.SELECT_OBJECT_HINT));
        }
    }

    public void showObjectInfo(HumanBeingUiModel h) {
        if (h == null) {
            objectInfo.setText(Messages.get(Messages.Key.SELECT_OBJECT_HINT));
            return;
        }

        objectInfo.setText("id=" + h.id()
                + ", name=" + h.name()
                + ", coordinates=(" + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                + ", owner=" + h.ownerLogin()
                + ", mood=" + h.mood()
                + ", impactSpeed=" + h.impactSpeed()
                + ", minutes=" + h.minutesOfWaiting()
                + ", weapon=" + h.weaponType()
                + ", car=" + h.car().name() + " / cool=" + h.car().cool());
    }
}
