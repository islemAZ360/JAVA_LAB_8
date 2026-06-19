package main.java.client.gui.pages.dashboard.prototype;

import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.empty.UiEmpty;
import main.java.client.gui.core.Messages;
import main.java.client.gui.model.HumanBeingUiModel;
import main.java.client.gui.mockup.ObjectVisualizationCanvas;
import javafx.scene.control.Label;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Visualization panel for prototype dashboard.
 * Contains: canvas, hint, object info label.
 *
 * Note: This is an internal component of PrototypePage.
 */
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

    /**
     * Refresh all text labels based on current language.
     */
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

    /**
     * Display detailed information about selected object.
     */
    public void showObjectInfo(HumanBeingUiModel h) {
        if (h == null) {
            objectInfo.setText(Messages.get(Messages.Key.SELECT_OBJECT_HINT));
            return;
        }

        // Build object info string with i18n labels
        String info = Messages.get(Messages.Key.OBJECT_INFO_ID) + "= " + h.id()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_NAME) + "= " + h.name()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_COORDINATES) + "= ("
                + h.coordinates().x() + "; " + h.coordinates().y() + ")"
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_OWNER) + "= " + h.ownerLogin()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_IMPACT_SPEED) + "= " + h.impactSpeed()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_MINUTES) + "= " + h.minutesOfWaiting()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_WEAPON) + "= " + h.weaponType()
                + ", " + Messages.get(Messages.Key.OBJECT_INFO_CAR) + "= " + h.car().name()
                + " / " + Messages.get(Messages.Key.OBJECT_INFO_COOL) + "= " + h.car().cool();

        objectInfo.setText(info);
    }
}
