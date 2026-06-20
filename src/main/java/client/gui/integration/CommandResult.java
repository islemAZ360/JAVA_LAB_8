package main.java.client.gui.integration;

// результат выполнения произвольной команды из терминала
public record CommandResult(String message, boolean success) {

    public static CommandResult ok(String message) {
        return new CommandResult(message == null ? "" : message, true);
    }

    public static CommandResult fail(String message) {
        return new CommandResult(message == null ? "" : message, false);
    }
}
