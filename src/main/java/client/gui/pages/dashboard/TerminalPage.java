package main.java.client.gui.pages.dashboard;

import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.client.gui.components.terminal.UiTerminalPanel;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.layout.BasePage;

public class TerminalPage extends BasePage {
    public TerminalPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_TERMINAL_TITLE),
                Messages.get(Messages.Key.PAGE_TERMINAL_DESCRIPTION)
        );
        this.buildContent();
    }

    private void buildContent() {

        UiTerminalPanel terminal = new UiTerminalPanel("UI Main");

        // Startup logs
        UiTerminalPanel.system("Process started.");
//        UiTerminalPanel.info("Logged in as: " + currentUser);

//        UiButton termBtn = new UiButton("Terminal", ButtonVariant.GHOST);
//        termBtn.setMaxWidth(Double.MAX_VALUE);
//        termBtn.getStyleClass().add("ui-sidebar-item");
//        termBtn.setOnAction(e -> terminal.toggle());
        StackPane terminalWrapper = new StackPane();
        terminalWrapper.getChildren().add(terminal);
//        Use Maximum Size Instead: For a node to resize properly inside standard layouts like BorderPane or VBox, you typically define its maximum limits rather than its preferred limits.
//        terminalWrapper.setMaxWidth(Double.MAX_VALUE);
//        terminalWrapper.setMaxHeight(Double.MAX_VALUE);
//        terminalWrapper.setPrefHeight(Double.MAX_VALUE);
//        Use Region.USE_COMPUTED_SIZE: If you want the terminal to shrink when space is constrained, set its size constraints back to the default Region values:
        terminalWrapper.setMaxHeight(Region.USE_COMPUTED_SIZE);
//        Add Resizing Constraints: If your terminal doesn't fill the wrapper correctly, enforce horizontal expansion using the StackPane constraints:
//        StackPane.setAlignment(terminal, Pos.BOTTOM_LEFT); // Or Pos.CENTER

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().addAll(spacer, terminalWrapper);

    }
}
