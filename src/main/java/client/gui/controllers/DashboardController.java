package main.java.client.gui.controllers;

import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.router.AppRouter;

public class DashboardController implements Controller {
    private final Lab7CommandGateway gateway;
    private final AppRouter router;

    public DashboardController (Lab7CommandGateway gateway, AppRouter router) {
        this.gateway = gateway;
        this.router = router;
    }

    public void listen() {

    }
}
