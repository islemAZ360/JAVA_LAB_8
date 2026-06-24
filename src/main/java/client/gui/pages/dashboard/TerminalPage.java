package main.java.client.gui.pages.dashboard;

import javafx.application.Platform;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.client.gui.components.terminal.UiTerminalPanel;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.CommandResult;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.layout.BasePage;

public class TerminalPage extends BasePage {

    private final Lab7CommandGateway gateway;

    public TerminalPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_TERMINAL_TITLE),
                Messages.get(Messages.Key.PAGE_TERMINAL_DESCRIPTION)
        );
        this.gateway = gateway;
        this.buildContent();
    }

    private void buildContent() {
        UiTerminalPanel terminal = new UiTerminalPanel("Terminal");

        // стартовые сообщения
        UiTerminalPanel.system("Terminal session started.");
        UiTerminalPanel.info("Type 'help' for available commands.");

        // обработчик команд из терминала
        terminal.setCommandHandler(this::executeCommandAsync);

        // включаем полностраничный режим: терминал заполняет всю страницу без анимаций
//        terminal.setFullPageMode(true);
//        terminal.showCommandInput(true);

        StackPane terminalWrapper = new StackPane();
        terminalWrapper.getChildren().add(terminal);
        terminalWrapper.setMaxHeight(Region.USE_COMPUTED_SIZE);
        terminalWrapper.setMinHeight(590);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().addAll(spacer, terminalWrapper);
    }

    // отправляем команду в фоновом потоке, чтобы не повесить UI
    private void executeCommandAsync(String command) {
        Thread worker = new Thread(() -> {
            try {
                CommandResult result = gateway.executeRawCommand(command);
                String msg = result.message();
                if (msg == null || msg.isBlank()) {
                    msg = result.success() ? "[OK]" : "[no response]";
                }
                // выводим результат в FX-потоке
                final String output = msg;
                if (result.success()) {
                    Platform.runLater(() -> UiTerminalPanel.success(output));
                } else {
                    Platform.runLater(() -> UiTerminalPanel.error(output));
                }
            } catch (Exception e) {
                // сервер недоступен или что-то сломалось — выводим красным
                String errMsg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                Platform.runLater(() -> UiTerminalPanel.error("[ERROR] Server connection failed: " + errMsg));
            }
        }, "terminal-command-worker");
        worker.setDaemon(true);
        worker.start();
    }
}
