package client.ui.template.core;

import javafx.scene.Scene;

import java.net.URL;
import java.util.List;

public final class CssLoader {
    private CssLoader() {}

    private static final List<String> STYLESHEETS = List.of(
            "/static/css/global.css",
            "/static/css/button.css",
            "/static/css/card.css",
            "/static/css/alert.css",
            "/static/css/form.css",
            "/static/css/data-table.css",
            "/static/css/sidebar.css",
            "/static/css/dialog.css",
            "/static/css/avatar.css",
            "/static/css/spinner.css",
            "/static/css/visualization.css"
    );

    public static void applyTo(Scene scene) {
        for (String css : STYLESHEETS) {
            URL url = CssLoader.class.getResource(css);
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            } else {
                System.err.println("CSS not found: " + css);
            }
        }
    }
}
