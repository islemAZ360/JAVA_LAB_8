package main.java.client.gui.integration;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;
import main.java.client.gui.components.alert.AlertVariant;
import main.java.client.gui.components.alert.UiAlert;
import main.java.client.gui.factory.UiComponentFactory;

import java.util.function.Consumer;


public class StreamingPreviewManager {

    private final IResultContainer view;
    private final Window owner;
    private final IncrementalJsonParser parser = new IncrementalJsonParser();


    private Region skeletonNode = null;

    private TableView<String[]> liveTable;
    private javafx.scene.control.TabPane liveTabPane;
    private VBox liveFormFields;
    private VBox liveDiffLines;
    private VBox liveOptions;
    private java.util.List<String> columnIds = new java.util.ArrayList<>();


    public StreamingPreviewManager(IResultContainer view, Window owner) {
        this.view = view;
        this.owner = owner;
        wireParser();
    }


    public Consumer<String> onToken() {
        return parser::feed;
    }

    public Consumer<String> onComplete() {
        return parser::complete;
    }

    public Consumer<String> onError() {
        return parser::error;
    }


    private void wireParser() {

        parser.onTypeDetected(type -> Platform.runLater(() -> showSkeleton(type)));

        parser.onItem((category, itemJson) -> Platform.runLater(() -> fillItem(category, itemJson)));

        parser.onComplete(fullJson -> Platform.runLater(() -> {
            if (skeletonNode != null) {
                view.removeResultComponent(skeletonNode);
                skeletonNode = null;
            }
            Region component = UiComponentFactory.create(fullJson, owner);
            view.addResultComponent(component);
        }));

        parser.onError(error -> Platform.runLater(() -> {
            if (skeletonNode != null) {
                view.removeResultComponent(skeletonNode);
                skeletonNode = null;
            }
            view.addResultComponent(new UiAlert("Connection Error", "Can not connect to server!", AlertVariant.ERROR));
        }));
    }


    private void showSkeleton(String type) {
        skeletonNode = switch (type) {
            case "table" -> buildTableSkeleton();
            case "tabs" -> buildTabsSkeleton();
            case "input_form" -> buildFormSkeleton();
            case "diff" -> buildDiffSkeleton();
            case "checkbox_group", "radio_group" -> buildOptionsSkeleton();
            default -> buildGenericSkeleton(type);
        };
        view.addResultComponent(skeletonNode);
    }

    private Region buildTableSkeleton() {
        VBox box = new VBox(0);
        box.setStyle("-fx-background-color: var(--color-background-secondary); -fx-background-radius: 8; -fx-padding: 12;");

        liveTable = new TableView<>();
        liveTable.setPlaceholder(new Label(""));
        liveTable.setPrefHeight(200);
        liveTable.setMaxWidth(Double.MAX_VALUE);
        columnIds.clear();

        box.getChildren().addAll(liveTable, spinner("Loading table..."));
        return box;
    }

    private Region buildTabsSkeleton() {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: var(--color-background-secondary); -fx-background-radius: 8; -fx-padding: 12;");

        liveTabPane = new TabPane();
        liveTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        liveTabPane.setPrefHeight(300);

        box.getChildren().addAll(liveTabPane, spinner("Loading tabs..."));
        return box;
    }


    private Region buildFormSkeleton() {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: var(--color-background-secondary); -fx-background-radius: 8; -fx-padding: 12;");

        liveFormFields = new VBox(8);
        box.getChildren().addAll(liveFormFields, spinner("Loading form..."));
        return box;
    }


    private Region buildDiffSkeleton() {
        VBox box = new VBox(0);
        box.setStyle("-fx-background-color: #0d1117; -fx-background-radius: 8; -fx-padding: 8;");

        liveDiffLines = new VBox(0);
        liveDiffLines.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");

        ScrollPane scroll = new ScrollPane(liveDiffLines);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(250);
        scroll.setStyle("-fx-background-color: transparent;");

        box.getChildren().addAll(scroll, spinner("Loading diff..."));
        return box;
    }


    private Region buildOptionsSkeleton() {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: var(--color-background-secondary); -fx-background-radius: 8; -fx-padding: 12;");

        liveOptions = new VBox(8);
        box.getChildren().addAll(liveOptions, spinner("Loading options..."));
        return box;
    }


    private Region buildGenericSkeleton(String type) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: var(--color-background-secondary); -fx-background-radius: 8; -fx-padding: 12;");
        box.getChildren().add(spinner("Generating " + type + "..."));
        return box;
    }


    private void fillItem(String category, String itemJson) {
        switch (category) {

            case "column" -> {
                if (liveTable == null) return;
                String id = UiComponentFactory.extractField(itemJson, "id");
                String label = UiComponentFactory.extractField(itemJson, "label");
                String wStr = UiComponentFactory.extractField(itemJson, "width");
                double width = wStr.isEmpty() ? 120 : Double.parseDouble(wStr);

                columnIds.add(id);
                int colIndex = columnIds.size() - 1;

                TableColumn<String[], String> col = new TableColumn<>(label);
                col.setPrefWidth(width);
                final int idx = colIndex;
                col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(idx < data.getValue().length ? data.getValue()[idx] : ""));
                liveTable.getColumns().add(col);
            }

            case "row" -> {
                if (liveTable == null || columnIds.isEmpty()) return;
                String[] row = new String[columnIds.size()];
                for (int i = 0; i < columnIds.size(); i++) {
                    row[i] = UiComponentFactory.extractField(itemJson, columnIds.get(i));
                }
                liveTable.getItems().add(row);
            }

            case "tab" -> {
                if (liveTabPane == null) return;
                String label = UiComponentFactory.extractField(itemJson, "label");
                Tab tab = new Tab(label);
                tab.setClosable(false);
                Label placeholder = new Label("Loading...");
                placeholder.setStyle("-fx-opacity: 0.4; -fx-padding: 16;");
                tab.setContent(placeholder);
                liveTabPane.getTabs().add(tab);
            }

            case "field" -> {
                if (liveFormFields == null) return;
                String label = UiComponentFactory.extractField(itemJson, "label");
                String placeholder = UiComponentFactory.extractField(itemJson, "placeholder");
                String type = UiComponentFactory.extractField(itemJson, "type");

                Label lbl = new Label(label);
                lbl.setStyle("-fx-font-size: 13px;");
                Control input = "textarea".equals(type) ? new TextArea() {{
                    setPromptText(placeholder);
                    setPrefHeight(80);
                }} : "password".equals(type) ? new PasswordField() {{
                    setPromptText(placeholder);
                }} : new TextField() {{
                    setPromptText(placeholder);
                }};
                input.setMaxWidth(Double.MAX_VALUE);

                liveFormFields.getChildren().addAll(lbl, input);
            }

            case "line" -> {
                if (liveDiffLines == null) return;
                String lineType = UiComponentFactory.extractField(itemJson, "type");
                String content = UiComponentFactory.extractField(itemJson, "content");

                HBox row = new HBox();
                Label prefix = new Label();
                Label text = new Label(content);
                text.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(text, Priority.ALWAYS);

                switch (lineType) {
                    case "added" -> {
                        prefix.setText("+ ");
                        prefix.setStyle("-fx-text-fill: #4ade80;");
                        text.setStyle("-fx-text-fill: #4ade80;");
                        row.setStyle("-fx-background-color: #1a3a1a; -fx-padding: 1 6 1 6;");
                    }
                    case "removed" -> {
                        prefix.setText("- ");
                        prefix.setStyle("-fx-text-fill: #f87171;");
                        text.setStyle("-fx-text-fill: #f87171;");
                        row.setStyle("-fx-background-color: #3a1a1a; -fx-padding: 1 6 1 6;");
                    }
                    default -> {
                        prefix.setText("  ");
                        row.setStyle("-fx-padding: 1 6 1 6;");
                    }
                }
                row.getChildren().addAll(prefix, text);
                liveDiffLines.getChildren().add(row);
            }

            case "option" -> {
                if (liveOptions == null) return;
                String label = UiComponentFactory.extractField(itemJson, "label");
                String checked = UiComponentFactory.extractField(itemJson, "checked");

                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                CheckBox cb = new CheckBox(label);
                cb.setSelected("true".equalsIgnoreCase(checked));
                cb.setDisable(true);
                row.getChildren().add(cb);
                liveOptions.getChildren().add(row);
            }
        }
    }


    private HBox spinner(String message) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new javafx.geometry.Insets(8, 0, 0, 0));

        ProgressIndicator pi = new ProgressIndicator(-1);
        pi.setPrefSize(14, 14);

        Label lbl = new Label(message);
        lbl.setStyle("-fx-font-size: 12px; -fx-opacity: 0.45;");

        row.getChildren().addAll(pi, lbl);
        return row;
    }


    public interface IResultContainer {
        void addResultComponent(Node node);

        void removeResultComponent(Node node);
    }
}
