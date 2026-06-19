package main.java.client.gui.components.terminal;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * UiTerminalPanel — overlay terminal panel, placed inside a StackPane.
 *
 * Overlay architecture (NOT using BorderPane.BOTTOM):
 *
 *   StackPane (Scene root)
 *   ├── MainShell (BorderPane) — full size, not constrained
 *   └── UiTerminalPanel       — floats at the bottom, overlays on top
 *         ├── [collapsed] only shows the 28px tab bar at the bottom
 *         └── [expanded]  log area slides up, overlays the content
 *
 * ── EMBEDDING in App.java ─────────────────────────────────────────────
 *
 *   // Replace the root StackPane with a wrapper containing the terminal
 *   StackPane root = new StackPane();
 *   Scene scene = new Scene(root, 1100, 720);
 *
 *   UiTerminalPanel terminal = UiTerminalPanel.install(root, "UI Main");
 *
 *   AppRouter router = new AppRouter(stage, gateway);
 *   router.showLogin();
 *
 * ── LOGGING from anywhere ─────────────────────────────────────────────
 *
 *   UiTerminalPanel.log("text");
 *   UiTerminalPanel.info("text");
 *   UiTerminalPanel.warn("text");
 *   UiTerminalPanel.error("text");
 *   UiTerminalPanel.success("text");
 *   UiTerminalPanel.system("text");
 *   UiTerminalPanel.debug("text");
 *   UiTerminalPanel.error("Failed", exception);  // + stack trace
 *
 * ── TOGGLE ────────────────────────────────────────────────────────────
 *
 *   UiTerminalPanel.get().toggle();   // from any button
 *   // or: Alt+F12 (scene-wide keyboard shortcut)
 *
 * ── COMMAND INPUT ─────────────────────────────────────────────────────
 *
 *   terminal.setCommandHandler(cmd -> {
 *       Response r = inputManager.handleCommand(cmd);
 *       UiTerminalPanel.log(r.getMessage());
 *   });
 */
public class UiTerminalPanel extends VBox {

    // ── Singleton ────────────────────────────────────────────────────────────
    private static UiTerminalPanel INSTANCE;

    public static UiTerminalPanel get() { return INSTANCE; }

    /**
     * Creates the terminal and attaches it to the Scene's root StackPane.
     * Call once in App.start() after creating the root.
     */
    public static UiTerminalPanel install(StackPane root, String tabName) {
        UiTerminalPanel panel = new UiTerminalPanel(tabName);
        root.getChildren().add(panel);

        // Alignment: always anchored to the bottom, full width
        StackPane.setAlignment(panel, Pos.BOTTOM_CENTER);

        // Ensure the terminal is always on top in the stack
        panel.toFront();

        // When root resizes → panel takes full width
        panel.prefWidthProperty().bind(root.widthProperty());
        panel.maxWidthProperty().bind(root.widthProperty());

        return panel;
    }

    // ── Log level ────────────────────────────────────────────────────────────
    public enum LogLevel {
        DEFAULT, INFO, WARN, ERROR, SUCCESS, SYSTEM, DEBUG
    }

    public record LogEntry(String message, LogLevel level, String timestamp) {}

    // ── Constants ────────────────────────────────────────────────────────────
    private static final double TAB_BAR_H  = 28;
    private static final double DEFAULT_H  = 800;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── State ────────────────────────────────────────────────────────────────
    private boolean isExpanded      = false;
    private boolean autoScroll    = true;
    private boolean wrapText      = false;
    private boolean showTimestamp = true;
    private double  panelHeight   = DEFAULT_H;

    // ── UI ───────────────────────────────────────────────────────────────────
    private final TextFlow  logArea     = new TextFlow();
    private final ScrollPane scrollPane = new ScrollPane(logArea);
    private final TextField inputField  = new TextField();
    private final VBox      bodyBox     = new VBox();
    private final Label     tabLabel    = new Label();
    private final Label     lineCount   = new Label("0 lines");

    // ── Data ─────────────────────────────────────────────────────────────────
    private final List<LogEntry>              history        = new ArrayList<>();
    private final List<String>                cmdHistory     = new ArrayList<>();
    private final ConcurrentLinkedQueue<LogEntry> pending   = new ConcurrentLinkedQueue<>();
    private int    historyIdx     = -1;
    private Consumer<String> commandHandler;

    // ═══════════════════════════════════════════════════════════════════════════
    //  Constructor (private — use install())
    // ═══════════════════════════════════════════════════════════════════════════

    public UiTerminalPanel(String tabName) {
        INSTANCE = this;
        getStyleClass().add("ui-terminal-panel");
        setFillWidth(true);

        // Collapsed state: only show the tab bar
        setPrefHeight(TAB_BAR_H);
        setMaxHeight(TAB_BAR_H);
        setMinHeight(TAB_BAR_H);

        // IMPORTANT: only intercept mouse events in the area where the terminal actually has UI
        // The transparent area (when collapsed) does not block clicks to the elements below
        setPickOnBounds(false);

        // ── Tab bar ─────────────────────────────────────────────────────────
        HBox tabBar = buildTabBar(tabName);

        // ── Body ─────────────────────────────────────────────────────────────
        buildBody();

        getChildren().addAll(bodyBox, tabBar);

        // ── Alt+F12 shortcut ─────────────────────────────────────────────────
        sceneProperty().addListener((obs, o, sc) -> {
            if (sc == null) return;
            sc.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.isAltDown() && e.getCode() == KeyCode.F12) {
                    toggle();
                    e.consume();
                }
            });
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Build tab bar
    // ═══════════════════════════════════════════════════════════════════════════

    private HBox buildTabBar(String tabName) {
        tabLabel.setText("▶  " + tabName);
        tabLabel.getStyleClass().add("ui-terminal-tab-label");

        HBox activeTab = new HBox(tabLabel);
        activeTab.getStyleClass().addAll("ui-terminal-tab", "ui-terminal-tab-active");
        activeTab.setAlignment(Pos.CENTER_LEFT);
        activeTab.setPadding(new Insets(0, 14, 0, 10));
        activeTab.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) toggle();
        });

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons — only visible when expanded
        Button clearBtn = actionBtn("✕ Clear", () -> clear());
        Button wrapBtn  = actionBtn("⊡ Wrap",  null);
        Button timeBtn  = actionBtn("⏱",       null);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("ui-terminal-close-btn");
        closeBtn.setOnAction(e -> collapse());

        wrapBtn.setOnAction(e -> {
            toggleWrap();
            wrapBtn.setText(wrapText ? "⊡ Wrap ✓" : "⊡ Wrap");
        });
        timeBtn.setOnAction(e -> {
            toggleTimestamp();
            timeBtn.setText(showTimestamp ? "⏱ ✓" : "⏱");
        });

        lineCount.getStyleClass().add("ui-terminal-line-count");
        lineCount.setVisible(false);

        HBox right = new HBox(4, lineCount, clearBtn, wrapBtn, timeBtn, closeBtn);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPadding(new Insets(0, 8, 0, 0));

        // Show action buttons only when expanded
        clearBtn.visibleProperty().bind(bodyBox.visibleProperty());
        clearBtn.managedProperty().bind(bodyBox.visibleProperty());
        wrapBtn.visibleProperty().bind(bodyBox.visibleProperty());
        wrapBtn.managedProperty().bind(bodyBox.visibleProperty());
        timeBtn.visibleProperty().bind(bodyBox.visibleProperty());
        timeBtn.managedProperty().bind(bodyBox.visibleProperty());
        lineCount.visibleProperty().bind(bodyBox.visibleProperty());
        lineCount.managedProperty().bind(bodyBox.visibleProperty());

        HBox bar = new HBox(activeTab, spacer, right);
        bar.getStyleClass().add("ui-terminal-tabbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMinHeight(TAB_BAR_H);
        bar.setMaxHeight(TAB_BAR_H);
        bar.setPrefHeight(TAB_BAR_H);

        // Tab bar always receives clicks (pickOnBounds=true is default)
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Build body (log area + input)
    // ═══════════════════════════════════════════════════════════════════════════

    private void buildBody() {
        // Log area
        logArea.getStyleClass().add("ui-terminal-log");
        logArea.setPadding(new Insets(6, 10, 6, 10));
        logArea.setLineSpacing(2);

        scrollPane.getStyleClass().add("ui-terminal-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Resize handle (drag to change panel height)
        Region resizeHandle = new Region();
        resizeHandle.getStyleClass().add("ui-terminal-resize-handle");
        resizeHandle.setPrefHeight(4);
        resizeHandle.setMaxWidth(Double.MAX_VALUE);
        installResizeDrag(resizeHandle);

        // Input row
        HBox inputRow = buildInputRow();

        bodyBox.getChildren().addAll(resizeHandle, scrollPane, inputRow);
        bodyBox.getStyleClass().add("ui-terminal-body");
        bodyBox.setVisible(false);
        bodyBox.setManaged(false);
        VBox.setVgrow(bodyBox, Priority.ALWAYS);
    }

    private HBox buildInputRow() {
        Label prompt = new Label(">>>");
        prompt.getStyleClass().add("ui-terminal-prompt");
        prompt.setPadding(new Insets(0, 8, 0, 10));

        inputField.getStyleClass().add("ui-terminal-input");
        inputField.setPromptText("Enter command...");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        inputField.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> submitCommand();
                case UP    -> navigateHistory(-1);
                case DOWN  -> navigateHistory(1);
                case L     -> { if (e.isControlDown()) clear(); }
                default    -> {}
            }
        });

        HBox row = new HBox(prompt, inputField);
        row.getStyleClass().add("ui-terminal-input-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 8, 2, 0));
        row.setMinHeight(30);
        row.setMaxHeight(30);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Toggle expand / collapse
    // ═══════════════════════════════════════════════════════════════════════════

    public void toggle() {
        if (isExpanded) collapse(); else expand();
    }

    public void expand() {
        if (isExpanded) return;
        isExpanded = true;
        tabLabel.setText("▼  " + tabLabel.getText().replaceAll("^[▶▼]\\s+", ""));

        // Prepare bodyBox for parallel animation (invisible but managed)
        bodyBox.setVisible(true);
        bodyBox.setManaged(true);
        bodyBox.setOpacity(0.0);

        // Allow the container to grow
        setMinHeight(TAB_BAR_H);
        setMaxHeight(Double.MAX_VALUE);

        double targetHeight = TAB_BAR_H + panelHeight;

        // Animate height and opacity simultaneously for smooth transition
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(prefHeightProperty(), TAB_BAR_H),
                        new KeyValue(bodyBox.opacityProperty(), 0.0)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(prefHeightProperty(), targetHeight, Interpolator.EASE_OUT),
                        new KeyValue(bodyBox.opacityProperty(), 1.0, Interpolator.EASE_OUT)
                )
        );

        timeline.setOnFinished(event -> {
            if (autoScroll) scrollToBottom();
            inputField.requestFocus();
        });
        timeline.play();
    }

    public void collapse() {
        if (!isExpanded) return;
        isExpanded = false;
        tabLabel.setText("▶  " + tabLabel.getText().replaceAll("^[▶▼]\\s+", ""));

        // Capture current dynamic height to avoid stuttering if interrupted
        double currentHeight = getPrefHeight();

        // Animate height reduction and fade-out simultaneously
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(prefHeightProperty(), currentHeight),
                        new KeyValue(bodyBox.opacityProperty(), bodyBox.getOpacity())
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(prefHeightProperty(), TAB_BAR_H, Interpolator.EASE_IN),
                        new KeyValue(bodyBox.opacityProperty(), 0.0, Interpolator.EASE_IN)
                )
        );

        timeline.setOnFinished(event -> {
            // Completely remove from layout calculations after animation ends
            bodyBox.setVisible(false);
            bodyBox.setManaged(false);

            // Lock the component bounds to collapsed state
            setMaxHeight(TAB_BAR_H);
            setMinHeight(TAB_BAR_H);
        });
        timeline.play();
    }


    // ═══════════════════════════════════════════════════════════════════════════
    //  Resize drag
    // ═══════════════════════════════════════════════════════════════════════════

    private void installResizeDrag(Region handle) {
        final double[] startY = {0};
        final double[] startH = {0};

        handle.setOnMousePressed(e -> {
            startY[0] = e.getScreenY();
            startH[0] = panelHeight;
            e.consume();
        });
        handle.setOnMouseDragged(e -> {
            if (!isExpanded) return;
            double delta = startY[0] - e.getScreenY(); // drag up → increase
            panelHeight = Math.max(80, Math.min(600, startH[0] + delta));
            setPrefHeight(TAB_BAR_H + panelHeight);
            e.consume();
        });
        handle.setCursor(javafx.scene.Cursor.N_RESIZE);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Logging
    // ═══════════════════════════════════════════════════════════════════════════

    public void appendLog(String message, LogLevel level) {
        String ts = LocalTime.now().format(TIME_FMT);
        LogEntry entry = new LogEntry(message, level, ts);
        history.add(entry);
        pending.add(entry);

        if (Platform.isFxApplicationThread()) {
            flushPending();
        } else {
            Platform.runLater(this::flushPending);
        }
    }

    private void flushPending() {
        LogEntry entry;
        int count = 0;
        while ((entry = pending.poll()) != null) {
            renderEntry(entry);
            count++;
        }
        if (count > 0) {
            lineCount.setText(history.size() + " lines");
            if (autoScroll) scrollToBottom();
        }
    }

    private void renderEntry(LogEntry entry) {
        if (showTimestamp) {
            Text ts = new Text("[" + entry.timestamp() + "] ");
            ts.getStyleClass().addAll("ui-terminal-text", "ui-terminal-ts");
            logArea.getChildren().add(ts);
        }
        Text msg = new Text(entry.message() + "\n");
        msg.getStyleClass().add("ui-terminal-text");
        msg.getStyleClass().add(switch (entry.level()) {
            case INFO    -> "ui-terminal-info";
            case WARN    -> "ui-terminal-warn";
            case ERROR   -> "ui-terminal-error";
            case SUCCESS -> "ui-terminal-success";
            case SYSTEM  -> "ui-terminal-system";
            case DEBUG   -> "ui-terminal-debug";
            default      -> "ui-terminal-default";
        });
        if (wrapText) msg.wrappingWidthProperty().bind(logArea.widthProperty().subtract(20));
        logArea.getChildren().add(msg);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Controls
    // ═══════════════════════════════════════════════════════════════════════════

    public void clear() {
        history.clear();
        logArea.getChildren().clear();
        lineCount.setText("0 lines");
    }

    public void toggleWrap() {
        wrapText = !wrapText;
        for (Node n : logArea.getChildren()) {
            if (n instanceof Text t) {
                if (wrapText) t.wrappingWidthProperty().bind(logArea.widthProperty().subtract(20));
                else          t.wrappingWidthProperty().unbind();
            }
        }
    }

    public void toggleAutoScroll() { autoScroll = !autoScroll; }

    public void toggleTimestamp() {
        showTimestamp = !showTimestamp;
        logArea.getChildren().clear();
        history.forEach(this::renderEntry);
        if (autoScroll) scrollToBottom();
    }

    public void setCommandHandler(Consumer<String> handler) { this.commandHandler = handler; }

    public void showCommandInput(boolean show) {
        // inputRow is last child of bodyBox
        if (!bodyBox.getChildren().isEmpty()) {
            Node last = bodyBox.getChildren().getLast();
            last.setVisible(show);
            last.setManaged(show);
        }
    }

    public void redirectSystemOut() {
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(original) {
            @Override public void println(String x)  { super.println(x);  appendLog(x == null ? "null" : x, LogLevel.DEFAULT); }
            @Override public void println(Object x)  { super.println(x);  appendLog(String.valueOf(x), LogLevel.DEFAULT); }
        });
    }

    public boolean isExpanded() { return isExpanded; }
    public List<LogEntry> getHistory() { return Collections.unmodifiableList(history); }
    public void setDefaultHeight(double h) { this.panelHeight = Math.max(80, h); }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Command input
    // ═══════════════════════════════════════════════════════════════════════════

    private void submitCommand() {
        String cmd = inputField.getText().trim();
        if (cmd.isBlank()) return;
        cmdHistory.add(0, cmd);
        historyIdx = -1;
        inputField.clear();
        appendLog(">>> " + cmd, LogLevel.DEFAULT);
        if (commandHandler != null) {
            new Thread(() -> commandHandler.accept(cmd)).start();
        }
    }

    private void navigateHistory(int dir) {
        if (cmdHistory.isEmpty()) return;
        historyIdx = Math.max(-1, Math.min(cmdHistory.size() - 1, historyIdx + dir));
        inputField.setText(historyIdx < 0 ? "" : cmdHistory.get(historyIdx));
        inputField.end();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Static API
    // ═══════════════════════════════════════════════════════════════════════════

    public static void log(String msg)                       { dispatch(msg, LogLevel.DEFAULT); }
    public static void log(String msg, LogLevel level)       { dispatch(msg, level);            }
    public static void info(String msg)                      { dispatch(msg, LogLevel.INFO);    }
    public static void warn(String msg)                      { dispatch(msg, LogLevel.WARN);    }
    public static void error(String msg)                     { dispatch(msg, LogLevel.ERROR);   }
    public static void success(String msg)                   { dispatch(msg, LogLevel.SUCCESS); }
    public static void system(String msg)                    { dispatch(msg, LogLevel.SYSTEM);  }
    public static void debug(String msg)                     { dispatch(msg, LogLevel.DEBUG);   }
    public static void error(String msg, Throwable t) {
        error(msg + ": " + t.getMessage());
        for (var el : t.getStackTrace()) debug("  at " + el);
    }

    private static void dispatch(String msg, LogLevel level) {
        if (INSTANCE != null) {
            INSTANCE.appendLog(msg, level);
        } else {
            System.out.println("[" + level + "] " + msg);
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private Button actionBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("ui-terminal-action-btn");
        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }
}
