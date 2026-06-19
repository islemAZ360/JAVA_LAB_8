package main.java.client.gui.controllers;

import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.router.AppRouter;

public class MainController implements Controller {
    private final Lab7CommandGateway gateway;
    private final AppRouter router;
    private final AuthController authController;
    private final DashboardController dashboardController;
    private final SettingsController settingsController;

    public MainController(Lab7CommandGateway gateway, AppRouter router) {
        this.gateway = gateway;
        this.router = router;
        this.authController = new AuthController(gateway, router);
        this.dashboardController = new DashboardController(gateway, router);
        this.settingsController = new SettingsController(gateway, router);
    }

    @Override
    public void listen() {

    }
}
