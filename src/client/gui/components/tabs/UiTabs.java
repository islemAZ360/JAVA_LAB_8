package client.gui.components.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * UiTabs — horizontal tabs (shadcn Tabs).
 *
 * Usage:
 *   UiTabs tabs = new UiTabs();
 *   tabs.addTab("Profile", profileNode);
 *   tabs.addTab("Settings", settingsNode);
 */
public class UiTabs extends TabPane {
    public UiTabs() {
        getStyleClass().add("ui-tabs");
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    public UiTabs addTab(String title, Node content) {
        Tab tab = new Tab(title, content);
        tab.getStyleClass().add("ui-tab");
        getTabs().add(tab);
        return this;
    }

    public UiTabs addTab(String title, Node content, Node graphic) {
        Tab tab = new Tab(title, content);
        tab.setGraphic(graphic);
        tab.getStyleClass().add("ui-tab");
        getTabs().add(tab);
        return this;
    }

    public void selectTab(int index) {
        if (index >= 0 && index < getTabs().size())
            getSelectionModel().select(index);
    }
}
