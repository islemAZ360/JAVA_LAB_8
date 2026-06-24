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

    public static UiTerminalPanel get() {
        return INSTANCE;
    }

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

        // ÉP CHIỀU CAO MẶC ĐỊNH LÊN 800PX TỪ CONTAINER GỐC ĐỂ KHÔNG BỊ TRÀN BÉ
        panel.setPrefHeight(800);
        panel.setMinHeight(800);

        return panel;
    }

    // ── Log level ────────────────────────────────────────────────────────────
    public enum LogLevel {
        DEFAULT, INFO, WARN, ERROR, SUCCESS, SYSTEM, DEBUG
    }

    public record LogEntry(String message, LogLevel level, String timestamp) {
    }

    // ── Constants ────────────────────────────────────────────────────────────
    private static final double TAB_BAR_H = 36;
    private static final double DEFAULT_H = 800; // Đổi mặc định lên hẳn 800px theo yêu cầu
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── State (Giữ nguyên toàn bộ biến gốc của bạn) ──────────────────────────
    private boolean isExpanded = true;
    private boolean autoScroll = true;
    private boolean wrapText = false;
    private boolean showTimestamp = true;
    private double panelHeight = DEFAULT_H;

    // ── UI Components ────────────────────────────────────────────────────────
    private final TabPane tabPane = new TabPane();
    private final String baseTabName;

    // Các thành phần trỏ động để phục vụ tương thích với code cũ bên ngoài gọi đến
    private TextFlow logArea;
    private ScrollPane scrollPane;
    private TextField inputField;
    private VBox bodyBox;
    private final Label tabLabel = new Label();
    private final Label lineCount = new Label("0 lines");

    // ── Data (Giữ nguyên cấu trúc lưu trữ gốc của bạn) ────────────────────────
    private final List<LogEntry> history = new ArrayList<>();
    private final List<String> cmdHistory = new ArrayList<>();
    private final ConcurrentLinkedQueue<LogEntry> pending = new ConcurrentLinkedQueue<>();
    private int historyIdx = -1;
    private Consumer<String> commandHandler;

    // ═══════════════════════════════════════════════════════════════════════════
    //  Class Thẻ Tab độc lập (Mỗi thẻ Tab tự quản lý luồng ngầm của riêng mình)
    // ═══════════════════════════════════════════════════════════════════════════
    private static class TerminalTab extends Tab {
        final TextFlow logArea = new TextFlow();
        final ScrollPane scrollPane = new ScrollPane(logArea);
        final TextField inputField = new TextField();
        final VBox bodyBox = new VBox();
        final List<LogEntry> tabHistory = new ArrayList<>();
        final List<String> tabCmdHistory = new ArrayList<>();
        Thread activeThread = null;

        TerminalTab(String title) {
            super(title);
            bodyBox.getStyleClass().add("ui-terminal-body");
            VBox.setVgrow(bodyBox, Priority.ALWAYS);
            setContent(bodyBox);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Constructor
    // ═══════════════════════════════════════════════════════════════════════════
    public UiTerminalPanel(String tabName) {
        INSTANCE = this;
        this.baseTabName = tabName;
        getStyleClass().add("ui-terminal-panel");
        setFillWidth(true);

        // Khóa chặt kích thước ban đầu cao lớn 800px để không bị layout cha ép nhỏ
        setPrefHeight(DEFAULT_H);
        setMinHeight(400);
        setMaxHeight(Double.MAX_VALUE);
        setPickOnBounds(false);

        // Cấu hình TabPane
        tabPane.getStyleClass().add("ui-terminal-tabpane");
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Thanh điều hướng macOS bên trên
        HBox macHeader = buildMacHeader();

        // Tạo mặc định Tab đầu tiên
        createNewTerminalTab(tabName);

        // Lắng nghe sự kiện chuyển Tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab instanceof TerminalTab tTab) {
                updateActiveInstance(tTab);
            }
        });

        getChildren().addAll(macHeader, tabPane);

        // Tự động dọn dẹp khi out khỏi Scene
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                autoDestroy();
                return;
            }

            // Dùng EventHandler để tránh nuốt phím Enter của ô Input
            newScene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.isAltDown() && e.getCode() == KeyCode.F12) {
                    createNewTerminalTab(baseTabName);
                    e.consume();
                }
            });
        });
    }

    private void updateActiveInstance(TerminalTab activeTab) {
        this.logArea = activeTab.logArea;
        this.scrollPane = activeTab.scrollPane;
        this.inputField = activeTab.inputField;
        this.bodyBox = activeTab.bodyBox;
        this.tabLabel.setText(activeTab.getText());
        this.lineCount.setText(activeTab.tabHistory.size() + " lines");
    }

    private void createNewTerminalTab(String title) {
        TerminalTab newTab = new TerminalTab(title);

        newTab.logArea.getStyleClass().add("ui-terminal-log");
        newTab.logArea.setPadding(new Insets(8, 12, 8, 12));
        newTab.logArea.setLineSpacing(2);

        newTab.scrollPane.getStyleClass().add("ui-terminal-scroll");
        newTab.scrollPane.setFitToWidth(true);
        newTab.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        newTab.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(newTab.scrollPane, Priority.ALWAYS);

        Region resizeHandle = new Region();
        resizeHandle.getStyleClass().add("ui-terminal-resize-handle");
        resizeHandle.setPrefHeight(4);
        resizeHandle.setMaxWidth(Double.MAX_VALUE);
        installResizeDrag(resizeHandle);

        HBox inputRow = buildInputRowForTab(newTab);
        newTab.bodyBox.getChildren().addAll(resizeHandle, newTab.scrollPane, inputRow);

        // Cấu hình sự kiện đóng Tab để hủy luồng ngầm lập tức
        newTab.setOnCloseRequest(e -> {
            if (newTab.activeThread != null && newTab.activeThread.isAlive()) {
                newTab.activeThread.interrupt();
            }
            newTab.tabHistory.clear();
            newTab.tabCmdHistory.clear();
            newTab.logArea.getChildren().clear();
        });

        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);

        // Ép cập nhật biến trỏ động ngay khoảnh khắc sinh Tab để tránh lỗi logArea bị null
        updateActiveInstance(newTab);

        Platform.runLater(() -> newTab.inputField.requestFocus());
    }

    private HBox buildMacHeader() {
        Region closeCircle = new Region();
        closeCircle.getStyleClass().addAll("mac-circle-btn", "mac-btn-close");
        Region minCircle = new Region();
        minCircle.getStyleClass().addAll("mac-circle-btn", "mac-btn-minimize");
        Region maxCircle = new Region();
        maxCircle.getStyleClass().addAll("mac-circle-btn", "mac-btn-maximize");
        HBox macButtons = new HBox(8, closeCircle, minCircle, maxCircle);
        macButtons.setAlignment(Pos.CENTER_LEFT);

        closeCircle.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                for (Tab tab : tabPane.getTabs()) {
                    if (tab instanceof TerminalTab tTab && tTab.activeThread != null) {
                        tTab.activeThread.interrupt();
                    }
                }
                tabPane.getTabs().clear();
                setVisible(false);
                setManaged(false);
            }
        });

        Button addTabBtn = new Button("＋");
        addTabBtn.getStyleClass().add("ui-terminal-add-tab-btn");
        addTabBtn.setOnAction(e -> createNewTerminalTab(baseTabName));

        HBox leftContainer = new HBox(12, macButtons, addTabBtn);
        leftContainer.setAlignment(Pos.CENTER_LEFT);


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button clearBtn = actionBtn("✕ Clear", () -> clear());
        Button wrapBtn = actionBtn("⊡ Wrap", null);
        Button timeBtn = actionBtn("⏱", null);
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("ui-terminal-close-btn");
        closeBtn.setOnAction(e -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab instanceof TerminalTab tTab) {
                if (tTab.activeThread != null && tTab.activeThread.isAlive()) {
                    tTab.activeThread.interrupt();
                }
                tTab.tabHistory.clear();
                tTab.tabCmdHistory.clear();
                tTab.logArea.getChildren().clear();
                tabPane.getTabs().remove(tTab);
            }
        });
        wrapBtn.setOnAction(e -> {
            toggleWrap();
            wrapBtn.setText(wrapText ? "⊡ Wrap ✓" : "⊡ Wrap");
        });
        timeBtn.setOnAction(e -> {
            toggleTimestamp();
            timeBtn.setText(showTimestamp ? "⏱ ✓" : "⏱");
        });
        lineCount.getStyleClass().add("ui-terminal-line-count");
        HBox right = new HBox(4, lineCount, clearBtn, wrapBtn, timeBtn, closeBtn);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPadding(new Insets(0, 8, 0, 0));
        HBox bar = new HBox(leftContainer, spacer, right);
        bar.getStyleClass().add("ui-terminal-tabbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 16, 0, 16));
        bar.setMinHeight(TAB_BAR_H);
        bar.setMaxHeight(TAB_BAR_H);
        bar.setPrefHeight(TAB_BAR_H);
        return bar;
    }

    private HBox buildInputRowForTab(TerminalTab tTab) {
        Label prompt = new Label(">>>");
        prompt.getStyleClass().add("ui-terminal-prompt");
        prompt.setPadding(new Insets(0, 8, 0, 12));
        tTab.inputField.getStyleClass().add("ui-terminal-input");
        tTab.inputField.setPromptText("Enter command...");
        HBox.setHgrow(tTab.inputField, Priority.ALWAYS);
        tTab.inputField.setOnKeyPressed(e -> {
            updateActiveInstance(tTab);
            if (e.getCode() == KeyCode.ENTER) {
                submitCommand();
                e.consume();
            } else if (e.getCode() == KeyCode.UP) {
                navigateHistory(-1);
                e.consume();
            } else if (e.getCode() == KeyCode.DOWN) {
                navigateHistory(1);
                e.consume();
            } else if (e.getCode() == KeyCode.L && e.isControlDown()) {
                clear();
                e.consume();
            }
        });
        HBox row = new HBox(prompt, tTab.inputField);
        row.getStyleClass().add("ui-terminal-input-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 8, 4, 0));
        row.setMinHeight(32);
        row.setMaxHeight(32);
        return row;
    }

    private void installResizeDrag(Region handle) {
        final double[] startY = {0};
        final double[] startH = {0};
        handle.setOnMousePressed(e -> {
            startY[0] = e.getScreenY();
            startH[0] = panelHeight;
            e.consume();
        });
        handle.setOnMouseDragged(e -> {
            double delta = startY[0] - e.getScreenY();
            panelHeight = Math.max(200, Math.min(1000, startH[0] + delta));
            setPrefHeight(panelHeight);
            e.consume();
        });
        handle.setCursor(javafx.scene.Cursor.N_RESIZE);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Logging (Sửa lỗi dứt điểm: Tìm đích danh LogArea của Tab đang mở để vẽ)
    // ═══════════════════════════════════════════════════════════════════════════
    public void appendLog(String message, LogLevel level) {
        String ts = LocalTime.now().format(TIME_FMT);
        LogEntry entry = new LogEntry(message, level, ts);
        history.add(entry);
        if (tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            activeTab.tabHistory.add(entry);
        }
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
        if (count > 0 && tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            lineCount.setText(activeTab.tabHistory.size() + " lines");
            if (autoScroll) scrollToBottom();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Logging (Đồng bộ hóa an toàn cho tất cả các luồng Log từ commandHandler đổ về)
    // ═══════════════════════════════════════════════════════════════════════════
    private void renderEntry(LogEntry entry) {
        // Lấy Tab đang hoạt động tại thời điểm hàm render được kích hoạt
        javafx.scene.control.Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (!(currentTab instanceof TerminalTab activeTab)) return;

        TextFlow targetLogArea = activeTab.logArea;
        if (targetLogArea == null) return;

        // Luôn luôn bọc trong Platform.runLater để an toàn đồ họa JavaFX
        Platform.runLater(() -> {
            if (showTimestamp) {
                Text ts = new Text("[" + entry.timestamp() + "] ");
                ts.getStyleClass().addAll("ui-terminal-text", "ui-terminal-ts");
                targetLogArea.getChildren().add(ts);
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
            if (wrapText) msg.wrappingWidthProperty().bind(targetLogArea.widthProperty().subtract(20));
            targetLogArea.getChildren().add(msg);
        });
    }

    private void scrollToBottom() {
        if (scrollPane != null) {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        }
    }

    // ── Các hàm Controls ──────────────────────────────────────────────────────
    public void toggle() {
    }

    public void expand() {
    }

    public void collapse() {
    }

    public void clear() {
        if (tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            activeTab.tabHistory.clear();
            activeTab.logArea.getChildren().clear();
        }
        lineCount.setText("0 lines");
    }

    public void toggleWrap() {
        wrapText = !wrapText;
        if (logArea == null) return;
        for (Node n : logArea.getChildren()) {
            if (n instanceof Text t) {
                if (wrapText) t.wrappingWidthProperty().bind(logArea.widthProperty().subtract(20));
                else t.wrappingWidthProperty().unbind();
            }
        }
    }

    public void toggleAutoScroll() {
        autoScroll = !autoScroll;
    }

    public void toggleTimestamp() {
        showTimestamp = !showTimestamp;
        if (logArea == null) return;
        logArea.getChildren().clear();
        if (tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            activeTab.tabHistory.forEach(this::renderEntry);
        }
        if (autoScroll) scrollToBottom();
    }

    public void setCommandHandler(Consumer<String> handler) {
        this.commandHandler = handler;
    }

    public void showCommandInput(boolean show) {
        if (bodyBox != null && !bodyBox.getChildren().isEmpty()) {
            Node last = bodyBox.getChildren().getLast();
            last.setVisible(show);
            last.setManaged(show);
        }
    }

    public void redirectSystemOut() {
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(original) {
            @Override
            public void println(String x) {
                super.println(x);
                appendLog(x == null ? "null" : x, LogLevel.DEFAULT);
            }

            @Override
            public void println(Object x) {
                super.println(x);
                appendLog(String.valueOf(x), LogLevel.DEFAULT);
            }
        });
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public List getHistory() {
        return java.util.Collections.unmodifiableList(history);
    }

    public void setDefaultHeight(double h) {
        this.panelHeight = Math.max(150, h);
    }

    // ── Command Input Handling ───────────────────────────────────────────────
    private void submitCommand() {
        if (inputField == null) return;
        String cmd = inputField.getText().trim();
        if (cmd.isBlank()) return;

        if (tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            activeTab.tabCmdHistory.add(0, cmd);
            cmdHistory.add(0, cmd);
            inputField.clear();

            // 1. In chữ trắng câu lệnh người dùng nhập
            appendLog(">>> " + cmd, LogLevel.DEFAULT);

            if (commandHandler != null) {
                if (activeTab.activeThread != null && activeTab.activeThread.isAlive()) {
                    activeTab.activeThread.interrupt();
                }

                final String targetThreadName = "TerminalThread-" + activeTab.getText();

                // 2. ÉP IN CHỮ TÍM HỆ THỐNG VÀO ĐÚNG LOGAREA CỦA TAB HIỆN TẠI TRƯỚC KHI CHẠY LUỒNG
                Platform.runLater(() -> {
                    Text ts = new Text("[" + LocalTime.now().format(TIME_FMT) + "] ");
                    ts.getStyleClass().addAll("ui-terminal-text", "ui-terminal-ts");

                    Text msg = new Text("[System] " + targetThreadName + " was activated.\n");
                    msg.getStyleClass().addAll("ui-terminal-text", "ui-terminal-system"); // Ăn theo màu tím system

                    if (wrapText) msg.wrappingWidthProperty().bind(activeTab.logArea.widthProperty().subtract(20));

                    // Thêm trực tiếp vào logArea của tab đang hoạt động, không sợ bị null hay chậm luồng
                    activeTab.logArea.getChildren().addAll(ts, msg);
                    if (autoScroll) scrollToBottom();
                });

                // Khởi chạy luồng ngầm thực thi lệnh
                activeTab.activeThread = new Thread(() -> {
                    try {
                        commandHandler.accept(cmd);
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            if (Thread.currentThread().isInterrupted()) {
                                appendLog("[System] Command process was terminated.", LogLevel.WARN);
                            } else {
                                appendLog("[Error] " + ex.getMessage(), LogLevel.ERROR);
                            }
                        });
                    }
                });
                activeTab.activeThread.setName(targetThreadName);
                activeTab.activeThread.start();
            }
        }
    }

    private void navigateHistory(int dir) {
        if (tabPane.getSelectionModel().getSelectedItem() instanceof TerminalTab activeTab) {
            if (activeTab.tabCmdHistory.isEmpty()) return;
            historyIdx = Math.max(-1, Math.min(activeTab.tabCmdHistory.size() - 1, historyIdx + dir));
            inputField.setText(historyIdx < 0 ? "" : activeTab.tabCmdHistory.get(historyIdx));
            inputField.end();
        }
    }

    private Button actionBtn(String text, Runnable action) {
        Button btn = new Button(text);
        if (action != null) btn.setOnAction(e -> action.run());
        return btn;
    }

    private void autoDestroy() {
        history.clear();
        cmdHistory.clear();
        pending.clear();
        commandHandler = null;
        if (tabPane != null) {
            for (Tab tab : tabPane.getTabs()) {
                if (tab instanceof TerminalTab tTab) {
                    if (tTab.activeThread != null) tTab.activeThread.interrupt();
                    tTab.tabHistory.clear();
                    tTab.tabCmdHistory.clear();
                    tTab.logArea.getChildren().clear();
                }
            }
            tabPane.getTabs().clear();
        }
        getChildren().clear();
        if (INSTANCE == this) INSTANCE = null;
    }

//     ═══════════════════════════════════════════════════════════════════════════
//      Static API
//     ═══════════════════════════════════════════════════════════════════════════

    public static void log(String msg) {
        dispatch(msg, LogLevel.DEFAULT);
    }

    public static void log(String msg, LogLevel level) {
        dispatch(msg, level);
    }

    public static void info(String msg) {
        dispatch(msg, LogLevel.INFO);
    }

    public static void warn(String msg) {
        dispatch(msg, LogLevel.WARN);
    }

    public static void error(String msg) {
        dispatch(msg, LogLevel.ERROR);
    }

    public static void success(String msg) {
        dispatch(msg, LogLevel.SUCCESS);
    }

    public static void system(String msg) {
        dispatch(msg, LogLevel.SYSTEM);
    }

    public static void debug(String msg) {
        dispatch(msg, LogLevel.DEBUG);
    }

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
}
