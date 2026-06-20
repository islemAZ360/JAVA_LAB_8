package main.java.client.gui.components.sidebar;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UiSidebar extends VBox {
    private final VBox menu = new VBox(6);
    private final VBox footer = new VBox(6);
    private final List<UiButton> buttons = new ArrayList<>();
    // поставщики текста для каждого пункта — нужны, чтобы обновлять язык на лету
    private final List<Supplier<String>> buttonTextSuppliers = new ArrayList<>();

    public UiSidebar(String title) {
        getStyleClass().add("ui-sidebar");
        Label titleLabel = new Label(title == null ? "Menu" : title);
        titleLabel.getStyleClass().add("ui-sidebar-title");
        menu.getStyleClass().add("ui-sidebar-menu");
        footer.getStyleClass().add("ui-sidebar-footer");

        getChildren().addAll(titleLabel, menu, footer);
        VBox.setVgrow(menu, Priority.ALWAYS);
    }

    public UiSidebar addItem(String text, Runnable action) {
        return addItem(() -> text, action);
    }

    // вариант с поставщиком текста: при смене языка текст пересчитывается
    public UiSidebar addItem(Supplier<String> textSupplier, Runnable action) {
        UiButton button = new UiButton(textSupplier.get(), ButtonVariant.GHOST);
        button.getStyleClass().add("ui-sidebar-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            setSelected(button);
            if (action != null) action.run();
        });
        buttons.add(button);
        buttonTextSuppliers.add(textSupplier);
        menu.getChildren().add(button);
        if (buttons.size() == 1) {
            setSelected(button);
        }
        return this;
    }

    public UiSidebar setFooter(Node node) {
        footer.getChildren().setAll(node);
        return this;
    }

    // обновляем текст всех пунктов меню по их поставщикам
    public void refreshLanguage() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setText(buttonTextSuppliers.get(i).get());
        }
    }

    private void setSelected(UiButton selected) {
        for (UiButton button : buttons) {
            button.getStyleClass().remove("ui-sidebar-item-selected");
        }
        selected.getStyleClass().add("ui-sidebar-item-selected");
    }
}
