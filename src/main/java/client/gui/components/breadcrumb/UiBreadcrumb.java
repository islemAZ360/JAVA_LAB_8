package main.java.client.gui.components.breadcrumb;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.util.List;

/**
 * UiBreadcrumb — navigation breadcrumb (shadcn Breadcrumb).
 *
 * Usage:
 *   UiBreadcrumb.of(
 *       new Crumb("Home", () -> goHome()),
 *       new Crumb("Settings", () -> goSettings()),
 *       new Crumb("Profile", null)   // current, non-clickable
 *   )
 */
public class UiBreadcrumb extends HBox {

    public record Crumb(String label, Runnable action) {}

    private UiBreadcrumb() {
        getStyleClass().add("ui-breadcrumb");
        setSpacing(4);
    }

    public static UiBreadcrumb of(Crumb... crumbs) {
        UiBreadcrumb bc = new UiBreadcrumb();
        for (int i = 0; i < crumbs.length; i++) {
            Crumb c = crumbs[i];
            boolean isLast = i == crumbs.length - 1;
            if (isLast || c.action() == null) {
                Label lbl = new Label(c.label());
                lbl.getStyleClass().add(isLast ? "ui-breadcrumb-current" : "ui-breadcrumb-item");
                bc.getChildren().add(lbl);
            } else {
                Hyperlink link = new Hyperlink(c.label());
                link.getStyleClass().add("ui-breadcrumb-link");
                link.setOnAction(e -> c.action().run());
                bc.getChildren().add(link);
            }
            if (!isLast) {
                Label sep = new Label("/");
                sep.getStyleClass().add("ui-breadcrumb-sep");
                bc.getChildren().add(sep);
            }
        }
        return bc;
    }

    public static UiBreadcrumb of(List<Crumb> crumbs) {
        return of(crumbs.toArray(new Crumb[0]));
    }
}
