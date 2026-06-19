package main.java.client.gui.core;

import javafx.geometry.NodeOrientation;

public enum Direction {
    LTR(NodeOrientation.LEFT_TO_RIGHT),
    RTL(NodeOrientation.RIGHT_TO_LEFT);

    private final NodeOrientation orientation;

    Direction(NodeOrientation orientation) {
        this.orientation = orientation;
    }

    public NodeOrientation orientation() {
        return orientation;
    }
}
