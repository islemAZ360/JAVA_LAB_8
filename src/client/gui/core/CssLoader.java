package client.gui.core;

import javafx.scene.Scene;

import java.net.URL;
import java.util.List;

public final class CssLoader {
    private CssLoader() {}

    private static final List<String> STYLESHEETS = List.of(
            "/client/gui/resources/static/css/global.css",
            "/client/gui/resources/static/css/button.css",
            "/client/gui/resources/static/css/card.css",
            "/client/gui/resources/static/css/alert.css",
            "/client/gui/resources/static/css/form.css",
            "/client/gui/resources/static/css/data-table.css",
            "/client/gui/resources/static/css/sidebar.css",
            "/client/gui/resources/static/css/dialog.css",
            "/client/gui/resources/static/css/avatar.css",
            "/client/gui/resources/static/css/spinner.css",
            "/client/gui/resources/static/css/visualization.css"
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
