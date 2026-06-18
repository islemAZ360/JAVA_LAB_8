package client.gui.components.command;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Window;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * UiCommand — command palette / cmdk (shadcn Command).
 * Shows a searchable list of actions, triggered by keyboard shortcut.
 *
 * Usage:
 *   UiCommand cmd = new UiCommand(stage);
 *   cmd.addGroup("Actions")
 *      .add("New File",    "Ctrl+N", () -> newFile())
 *      .add("Open...",     "Ctrl+O", () -> openFile())
 *      .add("Save",        "Ctrl+S", () -> save());
 *   cmd.addGroup("Navigation")
 *      .add("Go to Dashboard", null, () -> goHome());
 *
 *   // Open with Ctrl+K
 *   scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
 *       if (e.isControlDown() && e.getCode() == KeyCode.K) { cmd.show(); e.consume(); }
 *   });
 */
public class UiCommand {
    private final Popup popup = new Popup();
    private final TextField search = new TextField();
    private final ListView<CommandItem> list = new ListView<>();
    private final List<CommandItem> allItems = new ArrayList<>();

    public record CommandItem(String group, String label, String shortcut, Runnable action) {}

    public UiCommand(Window owner) {
        VBox root = new VBox(0);
        root.getStyleClass().add("ui-command");
        root.setPrefWidth(520);
        root.setMaxHeight(420);

        search.getStyleClass().add("ui-command-input");
        search.setPromptText("Search commands...");
        search.setPadding(new Insets(12, 16, 12, 16));

        list.getStyleClass().add("ui-command-list");
        list.setPrefHeight(340);
        list.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(CommandItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); }
                else {
                    HBox row = new HBox(8);
                    Label lbl = new Label(item.label()); lbl.getStyleClass().add("ui-command-label");
                    HBox.setHgrow(lbl, Priority.ALWAYS);
                    row.getChildren().add(lbl);
                    if (item.shortcut() != null) {
                        Label sc = new Label(item.shortcut()); sc.getStyleClass().add("ui-command-shortcut");
                        row.getChildren().add(sc);
                    }
                    setGraphic(row); setText(null);
                }
            }
        });

        search.textProperty().addListener((obs, o, q) -> filter(q));
        list.setOnMouseClicked(e -> { if (e.getClickCount() == 2) execute(); });
        list.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) execute(); });
        search.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) popup.hide();
            else if (e.getCode() == KeyCode.DOWN) list.requestFocus();
        });

        root.getChildren().addAll(search, new javafx.scene.control.Separator(), list);
        popup.getContent().add(root);
        popup.setAutoHide(true);
        popup.setOnShown(e -> {
            search.clear();
            filter("");
            search.requestFocus();
            var b = owner.getX();
            popup.setX(owner.getX() + (owner.getWidth() - 520) / 2);
            popup.setY(owner.getY() + owner.getHeight() * 0.15);
        });
    }

    public GroupBuilder addGroup(String name) { return new GroupBuilder(name); }
    public void show() { if (!popup.isShowing()) popup.show(popup.getOwnerWindow()); }
    public void show(Window owner) { popup.show(owner); }
    public void hide() { popup.hide(); }

    private void filter(String q) {
        List<CommandItem> filtered = q == null || q.isBlank() ? allItems
            : allItems.stream().filter(i -> i.label().toLowerCase().contains(q.toLowerCase())).collect(Collectors.toList());
        list.getItems().setAll(filtered);
        if (!filtered.isEmpty()) list.getSelectionModel().select(0);
    }

    private void execute() {
        CommandItem item = list.getSelectionModel().getSelectedItem();
        if (item != null) { popup.hide(); item.action().run(); }
    }

    public class GroupBuilder {
        private final String group;
        GroupBuilder(String g) { this.group = g; }
        public GroupBuilder add(String label, String shortcut, Runnable action) {
            allItems.add(new CommandItem(group, label, shortcut, action));
            return this;
        }
    }
}
