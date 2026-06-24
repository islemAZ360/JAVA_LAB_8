package main.java.client.gui.components.button;

import javafx.event.Event;
import javafx.event.EventType;
import java.util.Map;

/**
 * Event fired from the outermost Component Factory layer when all forms/tabs are completed.
 * Used to notify the Controller to handle data transmission to the Gateway.
 */
public class GatewaySubmitEvent extends Event {

    /**
     * Defines the unique event type for Gateway submission filtering and handling.
     */
    public static final EventType<GatewaySubmitEvent> GATEWAY_SUBMIT_TYPE =
            new EventType<>(Event.ANY, "GATEWAY_SUBMIT");

    private final Map<String, Object> finalData;

    /**
     * Constructs a new GatewaySubmitEvent with the final aggregated data payload.
     *
     * @param finalData The map containing all collected form/tab data
     */
    public GatewaySubmitEvent(Map<String, Object> finalData) {
        super(GATEWAY_SUBMIT_TYPE);
        this.finalData = finalData;
    }

    /**
     * Retrieves the final aggregated data payload to be sent to the Gateway.
     *
     * @return The map of complete submission data
     */
    public Map<String, Object> getFinalData() {
        return finalData;
    }
}
