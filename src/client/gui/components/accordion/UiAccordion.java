package client.gui.components.accordion;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

/**
 * UiAccordion — collapsible sections (shadcn Accordion).
 *
 * Usage:
 *   UiAccordion a = new UiAccordion();
 *   a.addSection("What is this?", new Label("An accordion component."));
 *   a.addSection("How does it work?", contentNode);
 */
public class UiAccordion extends VBox {
    private final List<TitledPane> panes = new ArrayList<>();
    private boolean allowMultiple = false;

    public UiAccordion() {
        getStyleClass().add("ui-accordion");
        setSpacing(4);
    }

    public UiAccordion addSection(String title, Node content) {
        TitledPane pane = new TitledPane(title, content);
        pane.getStyleClass().add("ui-accordion-item");
        pane.setExpanded(false);
        if (!allowMultiple) {
            pane.expandedProperty().addListener((obs, o, n) -> {
                if (n) panes.stream().filter(p -> p != pane).forEach(p -> p.setExpanded(false));
            });
        }
        panes.add(pane);
        getChildren().add(pane);
        return this;
    }

    /** Allow multiple sections open simultaneously. */
    public UiAccordion allowMultiple(boolean v) { this.allowMultiple = v; return this; }
    public UiAccordion expandFirst() { if (!panes.isEmpty()) panes.get(0).setExpanded(true); return this; }
}
