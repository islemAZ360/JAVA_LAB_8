package main.java.client.gui.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import main.java.client.gui.components.button.GatewaySubmitEvent;
import main.java.client.gui.factory.UiComponentFactory;
import main.java.client.gui.integration.LLMGateWay;
import main.java.client.gui.integration.LLMGatewayStream;
import main.java.client.gui.integration.StreamingPreviewManager;
import main.java.client.gui.pages.dashboard.BrowserPage;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserPageController {
    private static final Logger logger = Logger.getLogger(BrowserPageController.class.getName());

    private final BrowserPage view;
    private final LLMGateWay llmGateway;
    private final LLMGatewayStream streamGateway;
    private boolean isStreaming = true;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BrowserPageController(LLMGateWay llmGateway, LLMGatewayStream streamGateway) {
        this.llmGateway = llmGateway;
        this.streamGateway = streamGateway;
        this.view = new BrowserPage(llmGateway, streamGateway);
        this.initEventHandlers();
    }

    public BrowserPage getView() {
        return this.view;
    }

    private void initEventHandlers() {
        this.isStreaming = view.getStreamButton().isSelected();
        view.getStreamButton().setOnAction(e -> {
            isStreaming = view.getStreamButton().isSelected();
        });
        view.getSearchButton().setOnAction(event -> handleSearchAction(isStreaming));
        view.getSearchInput().setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleSearchAction(isStreaming);
            }
        });
    }

    private void handleSearchAction() {
        String query = view.getSearchInputText().trim();
        if (query.isEmpty()) return;

        view.addResultComponent(UiComponentFactory.create(
                buildAlertJson("Message", query, "INFO"), null
        ));
        view.clearSearchInputText();

        Window owner = view.getScene() != null ? view.getScene().getWindow() : null;


        HBox streamingLabel = spinner("Generating...");
        view.addResultComponent(streamingLabel);

        executor.submit(() -> streamGateway.sendQuery(
                query,


                token -> Platform.runLater(() -> {

                    Label spinningLabel = (Label) streamingLabel.lookup(".spinning-label");
                    String current = spinningLabel.getText();
                    spinningLabel.setText(
                            current.equals("Generating...") ? token : current + token
                    );
                    streamingLabel.setOpacity(1.0);
                }),


                json -> Platform.runLater(() -> {
                    view.removeResultComponent(streamingLabel);
                    Region component = UiComponentFactory.create(json, owner);
                    view.addResultComponent(component);
                }),

                error -> Platform.runLater(() -> {
                    view.removeResultComponent(streamingLabel);
                    view.addResultComponent(UiComponentFactory.create(
                            buildAlertJson("Connection Error",
                                    "Check connection to server!", "ERROR"),
                            null
                    ));
                })
        ));

    }

    private void handleSearchAction(boolean isStreaming) {
        String query = view.getSearchInputText().trim();
        if (query.isEmpty()) return;

        System.out.println(isStreaming);

        view.addResultComponent(UiComponentFactory.create(
                buildAlertJson("Message", query, "INFO"), null
        ));
        view.clearSearchInputText();

        Window owner = view.getScene() != null ? view.getScene().getWindow() : null;

        if (isStreaming) {
            HBox streamingLabel = spinner("Generating...");
            view.addResultComponent(streamingLabel);

            executor.submit(() -> streamGateway.sendQuery(
                    query,
                    token -> Platform.runLater(() -> {
                        Label spinningLabel = (Label) streamingLabel.lookup(".spinning-label");
                        if (spinningLabel != null) {
                            String current = spinningLabel.getText();
                            spinningLabel.setText(
                                    current.equals("Generating...") ? token : current + token
                            );
                        }
                        streamingLabel.setOpacity(1.0);
                    }),
                    json -> Platform.runLater(() -> {
                        view.removeResultComponent(streamingLabel);
                        Region component = UiComponentFactory.create(json, owner);
                        if (component != null) {

                            component.addEventHandler(GatewaySubmitEvent.GATEWAY_SUBMIT_TYPE, event -> {
                                Map<String, Object> finalData = (Map<String, Object>) (Map<?, ?>) event.getFinalData();
                                executeGatewaySubmission(finalData, isStreaming, owner);
                            });

                            view.addResultComponent(component);
                        }
                    }),
                    error -> Platform.runLater(() -> {
                        view.removeResultComponent(streamingLabel);
                        view.addResultComponent(UiComponentFactory.create(
                                buildAlertJson("Connection Error", "Check connection to server!", "ERROR"),
                                null
                        ));
                    })
            ));

        } else {
            HBox loadingLabel = spinner("Thinking...");
            view.addResultComponent(loadingLabel);

            executor.submit(() -> {
                try {
                    String json = llmGateway.sendQuery(query);

                    Region component = UiComponentFactory.create(json, owner);

                    if (component != null) {

                        component.addEventHandler(GatewaySubmitEvent.GATEWAY_SUBMIT_TYPE, event -> {
                            Map<String, Object> finalData = (Map<String, Object>) (Map<?, ?>) event.getFinalData();
                            executeGatewaySubmission(finalData, isStreaming, owner);
                        });
                    }

                    Platform.runLater(() -> {
                        view.removeResultComponent(loadingLabel);
                        if (component != null) {
                            view.addResultComponent(component);
                        }
                    });

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Non stream gateway error", e);
                    Platform.runLater(() -> {
                        view.removeResultComponent(loadingLabel);
                        view.addResultComponent(UiComponentFactory.create(
                                buildAlertJson("Connection Error", "Cannot reach non stream gateway.", "ERROR"),
                                null
                        ));
                    });
                }
            });
        }
    }

    private void executeGatewaySubmission(Map<String, Object> formData, boolean isStreaming, Window owner) {
        StringBuilder sb = new StringBuilder("User Form Data:\n");
        formData.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        String textPayload = sb.toString().trim();

        System.out.println("Executing submission to gateway, streaming=" + isStreaming);

        if (isStreaming) {
            Platform.runLater(() -> {
                triggerFormStreamAction(textPayload, isStreaming, owner);
            });
        } else {
            HBox loadingLabel = spinner("Thinking...");
            Platform.runLater(() -> view.addResultComponent(loadingLabel));

            executor.submit(() -> {
                try {
                    String responseJson = llmGateway.sendQuery(textPayload);

                    Platform.runLater(() -> {
                        view.removeResultComponent(loadingLabel);
                        Region nextUi = UiComponentFactory.create(responseJson, owner);
                        if (nextUi != null) {
                            nextUi.addEventHandler(GatewaySubmitEvent.GATEWAY_SUBMIT_TYPE, event -> {
                                Map<String, Object> finalData = (Map<String, Object>) (Map<?, ?>) event.getFinalData();
                                executeGatewaySubmission(finalData, isStreaming, owner);
                            });

                            view.addResultComponent(nextUi);
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        view.removeResultComponent(loadingLabel);
                        view.addResultComponent(UiComponentFactory.create(
                                buildAlertJson("Connection Error", "Cannot reach non stream gateway.", "ERROR"), null
                        ));
                    });
                    ex.printStackTrace();
                }
            });
        }
    }

    private HBox spinner(String message) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.BOTTOM_LEFT);
        row.setPadding(new Insets(8, 0, 0, 0));

        ImageView duckView = new ImageView("/main/resources/static/icons/yellow-duck.png");
        duckView.setFitWidth(16);
        duckView.setFitHeight(16);
        duckView.setPreserveRatio(true);

        ProgressIndicator pi = new ProgressIndicator(-1);
        pi.setPrefSize(10, 10);
        pi.setMinSize(10, 10);
        HBox.setHgrow(pi, Priority.NEVER);

        VBox iconWrapper = new VBox(2);
        iconWrapper.getChildren().addAll(pi, duckView);
        iconWrapper.setPrefSize(16, 16);
        iconWrapper.setMinSize(16, 16);
        iconWrapper.setAlignment(Pos.BOTTOM_LEFT);
        iconWrapper.setPadding(new Insets(0, 0, 4, 0));

        Label lbl = new Label(message);
        lbl.getStyleClass().add("spinning-label");
        lbl.setWrapText(true);
        lbl.setStyle("-fx-font-size: 12px; -fx-opacity: 0.45;");
        lbl.setPadding(new Insets(0, 16, 0, 0));
        HBox.setHgrow(lbl, Priority.ALWAYS);
        lbl.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(iconWrapper, lbl);
        return row;
    }

    private String buildAlertJson(String title, String message, String variant) {
        return String.format(
                "{\"type\":\"alert\",\"props\":{\"title\":\"%s\",\"message\":\"%s\",\"variant\":\"%s\"}}",
                title.replace("\"", "\\\""),
                message.replace("\"", "\\\""),
                variant
        );
    }

    private void triggerFormStreamAction(String payload, boolean isStreaming, Window owner) {
        HBox nextStreamingLabel = spinner("Processing form data...");
        view.addResultComponent(nextStreamingLabel);

        executor.submit(() -> streamGateway.sendQuery(
                payload,
                token -> Platform.runLater(() -> {
                    Label spinningLabel = (Label) nextStreamingLabel.lookup(".spinning-label");
                    if (spinningLabel != null) {
                        String current = spinningLabel.getText();
                        spinningLabel.setText(current.equals("Processing form data...") ? token : current + token);
                    }
                }),
                nextJson -> Platform.runLater(() -> {
                    view.removeResultComponent(nextStreamingLabel);
                    Region nextComponent = UiComponentFactory.create(nextJson, owner);
                    if (nextComponent != null) {
                        nextComponent.addEventHandler(GatewaySubmitEvent.GATEWAY_SUBMIT_TYPE, e -> {
                            Map<String, Object> finalData = (Map<String, Object>) (Map<?, ?>) e.getFinalData();
                            executeGatewaySubmission(finalData, isStreaming, owner);
                        });
                        view.addResultComponent(nextComponent);
                    }
                }),
                err -> Platform.runLater(() -> {
                    view.removeResultComponent(nextStreamingLabel);
                })
        ));
    }

}
