package main.java.client.gui.pages.dashboard;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import main.java.client.gui.components.animation.UiAnimation;
import main.java.client.gui.components.button.ButtonSize;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.checkbox.UiCheckbox;
import main.java.client.gui.components.input.UiInputGroup;
import main.java.client.gui.components.spinner.UiSpinner;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.LLMGateWay;
import main.java.client.gui.integration.LLMGatewayStream;
import main.java.client.gui.integration.StreamingPreviewManager;
import main.java.client.gui.layout.BasePage;

public class BrowserPage extends BasePage implements StreamingPreviewManager.IResultContainer {
    private UiInputGroup searchInput;
    private UiButton searchButton;
    private VBox resultContainer;
    //    private UiAnimation animationBox;
//    private UiSpinner uiSpinner;
    private UiCheckbox streamButton;


    public BrowserPage(LLMGateWay gateway, LLMGatewayStream llmGatewayStream) {
        super(
                Messages.get(Messages.Key.PAGE_BROWSER_TITLE),
                Messages.get(Messages.Key.PAGE_BROWSER_DESCRIPTION));

        // Create a standard String array with 36 slots
//        String[] animationIconPaths = new String[36];
//        for (int i = 1; i <= 36; i++) {
//            // Assign the path to the correct index (0 to 35)
//            animationIconPaths[i - 1] = "/main/resources/static/duck-ani/duck-ani" + i + ".png";
//        }
//        this.animationBox = new UiAnimation(32, 32, animationIconPaths);
//        this.uiSpinner = new UiSpinner();
        this.buildContent();
    }

    private void buildContent() {
        HBox searchBar = this.buildSearchBar();
        ScrollPane searchResultArea = this.buildSearchResultArea();
        this.getStyleClass().add("browser-page");
//        this.getChildren().addAll(searchBar, searchBar, searchBar, searchBar, searchResultArea, animationBox);

//        resultContainer.getChildren().add(animationBox);

//        StackPane page_wrapper = new StackPane();
//        page_wrapper.getChildren().addAll(searchResultArea);

        this.getChildren().addAll(searchBar, searchResultArea);
    }

    private HBox buildSearchBar() {
        searchInput = new UiInputGroup("Welcome to browser page...");
        HBox.setHgrow(searchInput.input(), Priority.ALWAYS);

        this.streamButton = new UiCheckbox("Stream");
        streamButton.getStyleClass().add("stream-button");

        this.searchButton = new UiButton("search", ButtonVariant.DEFAULT);
        searchButton.applySize(ButtonSize.DEFAULT);

        HBox searchBar = new HBox(20);
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBar.getChildren().addAll(searchInput, streamButton, searchButton);
        searchBar.getStyleClass().add("search-bar");
        return searchBar;
    }

    private ScrollPane buildSearchResultArea() {
        ScrollPane searchResultArea = new ScrollPane();
        searchResultArea.getStyleClass().add("search-result-area");
        setVGrow(searchResultArea);

        StackPane mainStackRoot = new StackPane();
        mainStackRoot.setMaxWidth(Double.MAX_VALUE);

        // The main container for search results (Acts as the inner VBox)
        resultContainer = new VBox(10);
        resultContainer.setMaxWidth(Double.MAX_VALUE);

        // The first child layer (Acts as Stack1 wrapping the VBox)
        StackPane stackResultContainer = new StackPane(resultContainer);
        stackResultContainer.setMaxWidth(Double.MAX_VALUE);

//        The second child layer (Acts as Stack2 for floating overlays, animations, or popups)
//        StackPane stack2 = new StackPane();
//        stack2.setMaxWidth(Double.MAX_VALUE);
//        stack2.setMouseTransparent(true);

//        Add layers in order: stackResultContainer at the bottom, stack2 directly on top
//        mainStackRoot.getChildren().addAll(stackResultContainer, stack2);
        mainStackRoot.getChildren().add(stackResultContainer);

        // Set the root StackPane as the content of the ScrollPane
        searchResultArea.setContent(mainStackRoot);

        // Forces the ScrollPane to stretch the main root StackPane to fill 100% of its width
        searchResultArea.setFitToWidth(true);

        // Listener to track when result components are added or removed dynamically
        resultContainer.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                // Triggered when a new Node is added to the VBox
                if (change.wasAdded()) {
                    for (Node newNode : change.getAddedSubList()) {
                        System.out.println("Node added to VBox: " + newNode.getClass().getSimpleName());
                        // logic duck go down
                    }
                }

                // Triggered when an existing Node is removed from the VBox
                if (change.wasRemoved()) {
                    for (Node removedNode : change.getRemoved()) {
                        System.out.println("Node removed from VBox: " + removedNode.getClass().getSimpleName());
                        // logic
                    }
                }
            }
        });

        return searchResultArea;
    }

    // IN DEVELOPMENT

    public void moveDownOne(VBox vbox, Node node) {
        int currentIndex = vbox.getChildren().indexOf(node);

        if (vbox.getChildren().size() >= 3) {
            int targetIndex = currentIndex + 1;
            Node nextNode = vbox.getChildren().get(targetIndex);
            vbox.getChildren().removeAll(node, nextNode);
            vbox.getChildren().addAll(nextNode, node);
        }
    }

    public void moveUpOne(VBox vbox, Node node) {
        int currentIndex = vbox.getChildren().indexOf(node);

        // empty
    }

    // --- SUPPLIER FOR CONTROLLER (GETTERS / ACTIONS) ---

    public UiButton getSearchButton() {
        return this.searchButton;
    }

    public UiCheckbox getStreamButton() {
        return this.streamButton;
    }

    public TextField getSearchInput() {
        // Get input
        return this.searchInput.input();
    }

    public String getSearchInputText() {
        // Get text in input
        return this.searchInput.input().getText();
    }

    public void clearResults() {
        this.resultContainer.getChildren().clear();
    }

    public void clearSearchInputText() {
        // Get input
        this.searchInput.input().clear();
    }

    // flush new node into result container
    public void addResultComponent(Node component) {
        this.resultContainer.getChildren().add(component);
    }

    public void removeResultComponent(Node component) {
        this.resultContainer.getChildren().remove(component);
    }

    // "Node component" -> "Node... components"
    public void addAllResultComponent(Node... components) {
        this.resultContainer.getChildren().addAll(components);
    }

}
