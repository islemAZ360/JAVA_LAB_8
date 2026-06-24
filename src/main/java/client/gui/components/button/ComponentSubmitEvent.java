package main.java.client.gui.components.button;

import javafx.event.Event;
import javafx.event.EventType;
import java.util.Map;

/**
 * Shared event dispatched when a Submit/Apply action occurs on any Dynamic Component.
 */
public class ComponentSubmitEvent extends Event {

    // Defines the event type for JavaFX filtering and registration
    public static final EventType<ComponentSubmitEvent> SUBMIT_TYPE =
            new EventType<>(Event.ANY, "COMPONENT_SUBMIT");

    // Holds the collected data payload (Form fields, sliders, checkboxes, etc.)
    private final Map<String, Object> data;

    public ComponentSubmitEvent(Map<String, Object> data) {
        super(SUBMIT_TYPE);
        this.data = data;
    }

    // Gets the raw data payload from the event source
    public Map<String, Object> getData() {
        return data;
    }
}
