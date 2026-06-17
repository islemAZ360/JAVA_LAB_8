package client.gui.pages.dashboard;

import client.gui.layout.BasePage;
import client.gui.core.Messages;
import client.gui.integration.Lab7CommandGateway;
import client.gui.mockup.MainPrototypeView;

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
