package client.ui.template.core;

import javafx.scene.Scene;

import java.net.URL;
import java.util.List;

public final class CssLoader {
    private CssLoader() {}

    private static final List<String> STYLESHEETS = List.of(
            "/client/ui/resources/static/css/global.css",
            "/client/ui/resources/static/css/button.css",
            "/client/ui/resources/static/css/card.css",
            "/client/ui/resources/static/css/alert.css",
            "/client/ui/resources/static/css/form.css",
            "/client/ui/resources/static/css/data-table.css",
            "/client/ui/resources/static/css/sidebar.css",
            "/client/ui/resources/static/css/dialog.css",
            "/client/ui/resources/static/css/avatar.css",
            "/client/ui/resources/static/css/spinner.css",
            "/client/ui/resources/static/css/visualization.css"
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
