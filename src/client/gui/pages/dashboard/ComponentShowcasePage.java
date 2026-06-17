package client.gui.pages.dashboard;

import client.gui.components.alert.AlertVariant;
import client.gui.components.alert.UiAlert;
import client.gui.components.avatar.UiAvatar;
import client.gui.components.button.ButtonSize;
import client.gui.components.button.ButtonVariant;
import client.gui.components.button.UiButton;
import client.gui.components.card.UiCard;
import client.gui.components.datatable.ColumnSpec;
import client.gui.components.datatable.UiDataTable;
import client.gui.components.dialog.UiDialog;
import client.gui.components.empty.UiEmpty;
import client.gui.components.field.UiField;
import client.gui.components.input.UiInputGroup;
import client.gui.components.resizable.ResizableSplitPane;
import client.gui.components.select.UiSelect;
import client.gui.components.spinner.UiSpinner;
import client.gui.core.Messages;
import client.gui.integration.Lab7CommandGateway;
import client.gui.integration.MockLab7CommandGateway;
import client.gui.model.HumanBeingUiModel;
import client.gui.layout.BasePage;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;

/**
 * Page 2: UI Component System.
 * Showcases all available UI components organized in cards.
 */
public class ComponentShowcasePage extends BasePage {

    private final Lab7CommandGateway gateway = new MockLab7CommandGateway();
    private final String currentUser = "demo_user";

    public ComponentShowcasePage() {
        super(
                Messages.get(Messages.Key.PAGE_COMPONENTS_TITLE),
                Messages.get(Messages.Key.PAGE_COMPONENTS_DESCRIPTION)
        );

        buildContent();
    }

    private void buildContent() {
        this.getChildren().addAll(
                buildAlertShowcase(),
                buildButtonShowcase(),
                buildFormShowcase(),
                buildCardEmptyAvatarSpinnerShowcase(),
                buildResizableDialogShowcase(),
                buildSmallTableShowcase()
        );
    }

    // ========================================
    // SHOWCASE 1: Alert variants
    // ========================================
    private UiCard buildAlertShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_ALERT_TITLE),
                Messages.get(Messages.Key.SHOWCASE_ALERT_DESC)
        );

        FlowPane alerts = new FlowPane(12, 12);
        alerts.getChildren().addAll(
                new UiAlert("Info", Messages.get(Messages.Key.ALERT_INFO_MSG), AlertVariant.INFO),
                new UiAlert("Success", Messages.get(Messages.Key.ALERT_SUCCESS_MSG), AlertVariant.SUCCESS),
                new UiAlert("Warning", Messages.get(Messages.Key.ALERT_WARNING_MSG), AlertVariant.WARNING),
                new UiAlert("Error", Messages.get(Messages.Key.ALERT_ERROR_MSG), AlertVariant.ERROR)
        );

        card.content().getChildren().add(alerts);
        return card;
    }

    // ========================================
    // SHOWCASE 2: Button variants and sizes (NO ICONS)
    // ========================================
    private UiCard buildButtonShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_BUTTON_TITLE),
                Messages.get(Messages.Key.SHOWCASE_BUTTON_DESC)
        );

        // Variants
        FlowPane variants = new FlowPane(10, 10);
        variants.getChildren().addAll(
                new UiButton("Default", ButtonVariant.DEFAULT),
                new UiButton("Secondary", ButtonVariant.SECONDARY),
                new UiButton("Outline", ButtonVariant.OUTLINE),
                new UiButton("Ghost", ButtonVariant.GHOST),
                new UiButton("Delete", ButtonVariant.DESTRUCTIVE)
        );

        // Sizes (no icon button - using text instead)
        FlowPane sizes = new FlowPane(10, 10);
        sizes.getChildren().addAll(
                new UiButton("Small").applySize(ButtonSize.SMALL),
                new UiButton("Default").applySize(ButtonSize.DEFAULT),
                new UiButton("Large").applySize(ButtonSize.LARGE),
                new UiButton("Icon").applySize(ButtonSize.ICON)  // Text, not icon
        );

        card.content().getChildren().addAll(
                new Label(Messages.get(Messages.Key.SHOWCASE_BUTTON_VARIANTS)),
                variants,
                new Label(Messages.get(Messages.Key.SHOWCASE_BUTTON_SIZES)),
                sizes
        );

        return card;
    }

    // ========================================
    // SHOWCASE 3: Form components
    // ========================================
    private UiCard buildFormShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_FORM_TITLE),
                Messages.get(Messages.Key.SHOWCASE_FORM_DESC)
        );

        UiInputGroup nameInput = new UiInputGroup("name",
                Messages.get(Messages.Key.FORM_NAME_PLACEHOLDER), null);
        UiInputGroup xInput = new UiInputGroup("x", "coordinates.x", null);
        UiInputGroup yInput = new UiInputGroup("y", "coordinates.y", null);

        UiSelect<String> weaponSelect = new UiSelect<>(List.of(
                "HAMMER", "SHOTGUN", "KNIFE", "MACHINE_GUN", "BAT"
        ));
        weaponSelect.selectFirstIfAny();

        UiSelect<String> moodSelect = new UiSelect<>(List.of(
                "SADNESS", "LONGING", "GLOOM", "CALM", "RAGE"
        ));
        moodSelect.selectFirstIfAny();

        CheckBox realHero = new CheckBox("realHero");
        CheckBox hasToothpick = new CheckBox("hasToothpick");
        HBox booleans = new HBox(12, realHero, hasToothpick);

        card.content().getChildren().addAll(
                new UiField("name", nameInput)
                        .setHelper(Messages.get(Messages.Key.FORM_NAME_HELPER)),
                new UiField("coordinates.x", xInput),
                new UiField("coordinates.y", yInput),
                new UiField("weaponType", weaponSelect),
                new UiField("mood", moodSelect),
                new UiField("boolean fields", booleans)
        );

        return card;
    }

    // ========================================
    // SHOWCASE 4: Card / Empty / Avatar / Spinner
    // ========================================
    private UiCard buildCardEmptyAvatarSpinnerShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_BASIC_TITLE),
                Messages.get(Messages.Key.SHOWCASE_BASIC_DESC)
        );

        UiAvatar avatar = new UiAvatar(currentUser).setRadius(24);
        UiSpinner spinner = new UiSpinner().setSize(42);

        UiEmpty empty = new UiEmpty(
                Messages.get(Messages.Key.EMPTY_TITLE),
                Messages.get(Messages.Key.EMPTY_DESCRIPTION)
        );
        empty.addAction(Messages.get(Messages.Key.EMPTY_ACTION_REFRESH), () -> {
            // Refresh logic placeholder
        });

        HBox top = new HBox(16, avatar, spinner);
        top.setAlignment(Pos.CENTER_LEFT);

        card.content().getChildren().addAll(top, empty);
        card.footer().getChildren().add(
                new UiButton(Messages.get(Messages.Key.SHOWCASE_FOOTER_ACTION), ButtonVariant.OUTLINE)
        );

        return card;
    }

    // ========================================
    // SHOWCASE 5: Resizable / Dialog
    // ========================================
    private UiCard buildResizableDialogShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_RESIZABLE_TITLE),
                Messages.get(Messages.Key.SHOWCASE_RESIZABLE_DESC)
        );

        UiCard left = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_LEFT_PANEL),
                Messages.get(Messages.Key.SHOWCASE_LEFT_PANEL_DESC)
        );
        left.content().getChildren().add(new Label("Table area"));

        UiCard right = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_RIGHT_PANEL),
                Messages.get(Messages.Key.SHOWCASE_RIGHT_PANEL_DESC)
        );
        right.content().getChildren().add(new Label("Canvas area"));

        ResizableSplitPane split = new ResizableSplitPane(
                Orientation.HORIZONTAL, left, right
        ).setDivider(0.5);
        split.setPrefHeight(180);

        UiButton openDialog = new UiButton(
                Messages.get(Messages.Key.SHOWCASE_OPEN_DIALOG),
                ButtonVariant.DEFAULT
        );
        openDialog.setOnAction(e -> {
            javafx.scene.Scene scene = card.getScene();
            if (scene != null) {
                UiDialog dialog = new UiDialog(
                        Messages.get(Messages.Key.SHOWCASE_DIALOG_TITLE),
                        scene.getWindow()
                );

                UiAlert alert = new UiAlert(
                        Messages.get(Messages.Key.SHOWCASE_DIALOG_COMPONENT),
                        Messages.get(Messages.Key.SHOWCASE_DIALOG_MSG),
                        AlertVariant.INFO
                );

                UiButton close = new UiButton(
                        Messages.get(Messages.Key.SHOWCASE_DIALOG_CLOSE),
                        ButtonVariant.SECONDARY
                );
                close.setOnAction(closeEvent -> dialog.close());

                dialog.content().getChildren().add(alert);
                dialog.footer().getChildren().add(close);
                dialog.showAndWait();
            }
        });

        card.content().getChildren().addAll(split, openDialog);
        return card;
    }

    // ========================================
    // SHOWCASE 6: Small Data Table
    // ========================================
    private UiCard buildSmallTableShowcase() {
        UiCard card = new UiCard(
                Messages.get(Messages.Key.SHOWCASE_TABLE_TITLE),
                Messages.get(Messages.Key.SHOWCASE_TABLE_DESC)
        );

        UiDataTable<HumanBeingUiModel> table = createHumanTable();
        table.setItems(gateway.show());
        table.setMaxHeight(260);

        card.content().getChildren().add(table);
        return card;
    }

    // ========================================
    // HELPER: Create HumanBeing table
    // ========================================
    private UiDataTable<HumanBeingUiModel> createHumanTable() {
        return new UiDataTable<HumanBeingUiModel>()
                .addColumn(new ColumnSpec<>("id", HumanBeingUiModel::id, 60,
                        Comparator.comparingLong(HumanBeingUiModel::id)))
                .addColumn(new ColumnSpec<>("name", HumanBeingUiModel::name, 120,
                        Comparator.comparing(HumanBeingUiModel::name)))
                .addColumn(new ColumnSpec<>("x", h -> h.coordinates().x(), 60))
                .addColumn(new ColumnSpec<>("y", h -> h.coordinates().y(), 60))
                .addColumn(new ColumnSpec<>("owner", HumanBeingUiModel::ownerLogin, 110));
    }
}
