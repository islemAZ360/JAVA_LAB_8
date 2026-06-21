package main.java.client.gui.pages.dashboard;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    private List<HumanBeingUiModel> allItems = new ArrayList<>();

    // ── UI-компоненты (инициализируются ДО создания listener'а) ──
    private final VBox itemContainer = new VBox();
    private final Label emptyLabel = new Label("No results found");

    // храним ссылку на поле ввода, чтобы оно не собралось GC
    private UiInputGroup searchInput;

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
        List<HumanBeingUiModel> serverData = gateway.show();
        allItems = (serverData != null) ? new ArrayList<>(serverData) : new ArrayList<>();

        System.out.println("[BrowserPage] Loaded " + allItems.size() + " items from server");

        // ⚠️ ВАЖНО: buildSearchResultArea() ПЕРЕД buildSearchBar()
        // чтобы itemContainer и emptyLabel были готовы ДО создания listener'а
        VBox searchResultArea = buildSearchResultArea();
        HBox searchBar = buildSearchBar();

        // первичная отрисовка списка
        renderItems(allItems);

        this.getChildren().addAll(searchBar, searchResultArea);
        // область результатов должна заполнять всю страницу по вертикали
        VBox.setVgrow(searchResultArea, Priority.ALWAYS);
    }

    private HBox buildSearchBar() {
        searchInput = new UiInputGroup("Welcome to browser page...");

        // TextField должен растягиваться внутри UiInputGroup
        TextField textField = searchInput.input();
        HBox.setHgrow(textField, Priority.ALWAYS);

        UiButton searchButton = new UiButton("search", ButtonVariant.DEFAULT);
        searchButton.applySize(ButtonSize.DEFAULT);

        // ── живой поиск: фильтруем при каждом изменении текста ──
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                System.out.println("[BrowserPage] Search: '" + oldVal + "' -> '" + newVal + "'");
                filterAndRender(newVal);
            } catch (Exception ex) {
                System.err.println("[BrowserPage] Error in live search listener: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // кнопка тоже запускает фильтр
        searchButton.setOnAction(e -> {
            try {
                filterAndRender(searchInput.getText());
            } catch (Exception ex) {
                System.err.println("[BrowserPage] Error in search button: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        HBox searchBar = new HBox(20);
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        searchBar.getChildren().addAll(searchInput, searchButton);
        return searchBar;
    }

    /**
     * Единый метод фильтрации — вызывается и из listener'а, и из кнопки.
     */
    private void filterAndRender(String rawQuery) {
        String q = (rawQuery == null) ? "" : rawQuery.toLowerCase(Locale.ROOT).trim();

        List<HumanBeingUiModel> filtered;
        if (q.isEmpty()) {
            // пустой запрос — показываем всё
            filtered = allItems;
        } else {
            filtered = allItems.stream()
                    .filter(h -> contains(h.name(), q) || contains(h.ownerLogin(), q))
                    .collect(Collectors.toList());
        }

        System.out.println("[BrowserPage] Query='" + q + "' → matched " + filtered.size() + "/" + allItems.size());
        renderItems(filtered);
    }

    private VBox buildSearchResultArea() {
        VBox searchResultArea = new VBox(6);
        searchResultArea.getStyleClass().add("search-result-area");
        searchResultArea.setMinHeight(200);

        itemContainer.setMinHeight(100);

        // заглушка когда нет результатов
        emptyLabel.getStyleClass().add("page-description");
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        ScrollPane scrollPane = new ScrollPane(itemContainer);
        scrollPane.setFitToWidth(true);
        // ⚠️ НЕ используем setFitToHeight(true) — оно ограничивает высоту содержимого
        // размером видимой области и мешает динамическому обновлению при фильтрации
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMinHeight(200);

        // скролл заполняет всё доступное пространство по вертикали
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        searchResultArea.getChildren().addAll(scrollPane, emptyLabel);
        return searchResultArea;
    }

    // перестраиваем список UiItem из отфильтрованных данных
    private void renderItems(List<HumanBeingUiModel> items) {
        if (itemContainer == null || emptyLabel == null) {
            System.err.println("[BrowserPage] renderItems called before UI init — skipping");
            return;
        }

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
        if (query == null || query.isBlank())
            return true;
        if (value == null)
            return false;
        return value.toLowerCase(Locale.ROOT).contains(query);
    }
}
