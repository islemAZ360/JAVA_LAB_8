package main.java.client.gui.pages.dashboard;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.java.client.gui.components.button.ButtonSize;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.input.UiInputGroup;
import main.java.client.gui.components.item.UiItem;
import main.java.client.gui.core.Messages;
import main.java.client.gui.integration.Lab7CommandGateway;
import main.java.client.gui.layout.BasePage;

import java.util.ArrayList;
import java.util.List;

public class BrowserPage extends BasePage {
    public BrowserPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_BROWSER_TITLE),
                Messages.get(Messages.Key.PAGE_BROWSER_DESCRIPTION));
        this.buildContent();
    }

    private void buildContent() {
//        Scene scene = new Scene(new StackPane(canvas), 800, 600);
//        primaryStage.setScene(scene);
//        primaryStage.show();
        HBox searchBar = this.buildSearchBar();
        VBox searchResultArea = this.buildSearchResultArea();
        this.getStyleClass().add("browser-page");
        this.getChildren().addAll(searchBar, searchResultArea);
    }

    private HBox buildSearchBar() {
        UiInputGroup searchInput = new UiInputGroup("Welcome to browser page...");
        HBox.setHgrow(searchInput.input(), Priority.ALWAYS);

        UiButton searchButton = new UiButton("search", ButtonVariant.DEFAULT);
        searchButton.applySize(ButtonSize.DEFAULT);

        HBox searchBar = new HBox(20);
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBar.getChildren().addAll(searchInput, searchButton);
        return searchBar;
    }

    private VBox buildSearchResultArea() {
        VBox searchResultArea = new VBox(6);
        searchResultArea.getStyleClass().add("search-result-area");

        // Mock data
        List<UiItem> itemList = new ArrayList<>();
        for (int i = 1; i <= 20 ; i++) {
            UiItem item = new UiItem("result " + i, "subtitle " + i);
            itemList.add(item);
        }

        VBox container = new VBox();
        container.getChildren().addAll(itemList);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true); // Ensures the container expands to fit the width

        // Make the ScrollPane and its inner viewport transparent
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");


        // OPTIMIZATION: Make the scroll pane expand vertically to fill the screen
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        searchResultArea.getChildren().addAll(scrollPane);
        return searchResultArea;
    }
}
