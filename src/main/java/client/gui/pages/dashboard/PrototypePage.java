package main.java.client.gui.pages.dashboard;

import main.java.client.gui.layout.BasePage;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.mockup.MainPrototypeView;

/**
 * Page 1: Lab 8 Prototype.
 * Wraps the MainPrototypeView inside a BasePage layout.
 */
public class PrototypePage extends BasePage {

    public PrototypePage(String currentUser, Lab7CommandGateway gateway, Runnable logout) {
        super(
                Messages.get(Messages.Key.PAGE_PROTOTYPE_TITLE),
                Messages.get(Messages.Key.PAGE_PROTOTYPE_DESCRIPTION)
        );

        MainPrototypeView prototypeView = new MainPrototypeView(currentUser, gateway, logout);
        this.getChildren().add(prototypeView);
        setVGrow(prototypeView);
    }
}
