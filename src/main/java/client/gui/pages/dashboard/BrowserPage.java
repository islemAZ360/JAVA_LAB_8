package main.java.client.gui.pages.dashboard;

import javafx.scene.control.Label;
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
import main.java.client.gui.model.HumanBeingUiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BrowserPage extends BasePage {

    private final Lab7CommandGateway gateway;
    private List<HumanBeingUiModel> allItems = List.of();
    private VBox itemContainer;
    private Label emptyLabel;

    public BrowserPage(Lab7CommandGateway gateway) {
        super(
                Messages.get(Messages.Key.PAGE_BROWSER_TITLE),
                Messages.get(Messages.Key.PAGE_BROWSER_DESCRIPTION));
        this.gateway = gateway;
        this.buildContent();
    }

    private void buildContent() {
        this.getStyleClass().add("browser-page");

        // получаем реальные данные с сервера
        allItems = gateway.show();
        if (allItems == null) allItems = List.of();

        HBox searchBar = buildSearchBar();
        VBox searchResultArea = buildSearchResultArea();

        // первичная отрисовка списка
        renderItems(allItems);

        this.getChildren().addAll(searchBar, searchResultArea);
        // область результатов должна заполнять всю страницу по вертикали
        VBox.setVgrow(searchResultArea, Priority.ALWAYS);
    }

    private HBox buildSearchBar() {
        UiInputGroup searchInput = new UiInputGroup("Welcome to browser page...");
        HBox.setHgrow(searchInput.input(), Priority.ALWAYS);

        UiButton searchButton = new UiButton("search", ButtonVariant.DEFAULT);
        searchButton.applySize(ButtonSize.DEFAULT);

        // живой поиск: фильтруем при каждом изменении текста
        searchInput.input().textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT).trim();
            List<HumanBeingUiModel> filtered;
            if (q.isEmpty()) {
                // пустой запрос — показываем всё
                filtered = allItems;
            } else {
                filtered = allItems.stream()
                        .filter(h -> contains(h.name(), q) || contains(h.ownerLogin(), q))
                        .collect(Collectors.toList());
            }
            renderItems(filtered);
        });

        // кнопка тоже запускает фильтр
        searchButton.setOnAction(e -> {
            String q = searchInput.getText() == null ? "" : searchInput.getText().toLowerCase(Locale.ROOT).trim();
            List<HumanBeingUiModel> filtered;
            if (q.isEmpty()) {
                filtered = allItems;
            } else {
                filtered = allItems.stream()
                        .filter(h -> contains(h.name(), q) || contains(h.ownerLogin(), q))
                        .collect(Collectors.toList());
            }
            renderItems(filtered);
        });

        HBox searchBar = new HBox(20);
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBar.getChildren().addAll(searchInput, searchButton);
        return searchBar;
    }

    private VBox buildSearchResultArea() {
        VBox searchResultArea = new VBox(6);
        searchResultArea.getStyleClass().add("search-result-area");
        searchResultArea.setMinHeight(200);

        itemContainer = new VBox();
        itemContainer.setMinHeight(100);

        // заглушка когда нет результатов
        emptyLabel = new Label("No results found");
        emptyLabel.getStyleClass().add("page-description");
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        ScrollPane scrollPane = new ScrollPane(itemContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMinHeight(200);

        // скролл заполняет всё доступное пространство по вертикали
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        searchResultArea.getChildren().addAll(scrollPane, emptyLabel);
        return searchResultArea;
    }

    // перестраиваем список UiItem из отфильтрованных данных
    private void renderItems(List<HumanBeingUiModel> items) {
        List<UiItem> uiItems = new ArrayList<>();
        for (HumanBeingUiModel h : items) {
            String title = h.name();
            String subtitle = "ID: " + h.id()
                    + " | Owner: " + h.ownerLogin()
                    + " | Speed: " + h.impactSpeed();
            uiItems.add(new UiItem(title, subtitle));
        }
        itemContainer.getChildren().setAll(uiItems);

        // показываем заглушку если список пуст
        boolean empty = uiItems.isEmpty();
        emptyLabel.setText(empty ? "No results found" : "");
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
    }

    // null-safe проверка вхождения подстроки
    private boolean contains(String value, String query) {
        if (query == null || query.isBlank()) return true;
        if (value == null) return false;
        return value.toLowerCase(Locale.ROOT).contains(query);
    }
}
