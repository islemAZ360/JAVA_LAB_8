package client.ui.template.mockup.dashboard;

import client.ui.template.core.Messages;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class DashboardNavigation extends VBox {
    private final Consumer<DashboardSection> onSectionChanged;
    private final Consumer<String> statusConsumer;

    private final Label titleLabel = new Label("HumanBeing");
    private final Label navLabel = new Label();
    private final TreeView<NavNode> treeView = new TreeView<>();

    private TreeItem<NavNode> collectionItem;
    private TreeItem<NavNode> commandsItem;
    private TreeItem<NavNode> visualizationItem;
    private TreeItem<NavNode> settingsItem;

    public DashboardNavigation(String currentUser,
                               Consumer<DashboardSection> onSectionChanged,
                               Consumer<String> statusConsumer) {
        this.onSectionChanged = onSectionChanged;
        this.statusConsumer = statusConsumer;

        getStyleClass().add("dashboard-nav");
        titleLabel.getStyleClass().add("ui-sidebar-title");
        navLabel.getStyleClass().add("dashboard-nav-label");

        treeView.getStyleClass().add("dashboard-nav-tree");
        treeView.setShowRoot(false);
        treeView.setMaxWidth(Double.MAX_VALUE);

        buildTree();
        treeView.setRoot(rootTree());
        treeView.getRoot().setExpanded(true);
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null || selected.getValue() == null || selected.getValue().section() == null) {
                return;
            }
            DashboardSection section = selected.getValue().section();
            if (onSectionChanged != null) {
                onSectionChanged.accept(section);
            }
            if (statusConsumer != null) {
                statusConsumer.accept(selected.getValue().statusText());
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(titleLabel, navLabel, treeView, spacer);
        refreshLanguage();
        treeView.getSelectionModel().select(collectionItem);
    }

    public void refreshLanguage() {
        DashboardSection selectedSection = selectedSection();

        navLabel.setText(Messages.get(Messages.Key.NAVIGATION));

        buildTree();
        TreeItem<NavNode> newRoot = rootTree();
        treeView.setRoot(newRoot);
        newRoot.setExpanded(true);

        TreeItem<NavNode> target = switch (selectedSection) {
            case COMMANDS -> commandsItem;
            case VISUALIZATION -> visualizationItem;
            case SETTINGS -> settingsItem;
            case COLLECTION -> collectionItem;
        };
        treeView.getSelectionModel().select(target);
    }

    private void buildTree() {
        collectionItem = leaf(Messages.get(Messages.Key.COLLECTION), DashboardSection.COLLECTION,
                "Object table is shown in the center area");
        commandsItem = leaf(Messages.get(Messages.Key.COMMANDS), DashboardSection.COMMANDS,
                "Commands are available through the bottom action bar");
        visualizationItem = leaf(Messages.get(Messages.Key.VISUALIZATION), DashboardSection.VISUALIZATION,
                "Visualization area is shown in the center area");
        settingsItem = leaf(Messages.get(Messages.Key.SETTINGS), DashboardSection.SETTINGS,
                "Theme and language are controlled in the top bar");
    }

    private TreeItem<NavNode> rootTree() {
        TreeItem<NavNode> root = new TreeItem<>(new NavNode("HumanBeing", null, ""));
        root.getChildren().setAll(collectionItem, commandsItem, visualizationItem, settingsItem);
        return root;
    }

    private TreeItem<NavNode> leaf(String title, DashboardSection section, String statusText) {
        return new TreeItem<>(new NavNode(title, section, statusText));
    }

    private DashboardSection selectedSection() {
        TreeItem<NavNode> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue() == null || selected.getValue().section() == null) {
            return DashboardSection.COLLECTION;
        }
        return selected.getValue().section();
    }

    private record NavNode(String title, DashboardSection section, String statusText) {
        @Override
        public String toString() {
            return title;
        }
    }
}
