package client.gui.core;

import javafx.scene.Scene;

import java.net.URL;
import java.util.List;

public final class CssLoader {
    private CssLoader() {}

    private static final List<String> STYLESHEETS = List.of(
//            Global css
            "/client/gui/resources/static/css/global.css",

//            Components css
            "/client/gui/resources/static/css/components/button.css",
            "/client/gui/resources/static/css/components/card.css",
            "/client/gui/resources/static/css/components/alert.css",
            "/client/gui/resources/static/css/components/form.css",
            "/client/gui/resources/static/css/components/data-table.css",
            "/client/gui/resources/static/css/components/sidebar.css",
            "/client/gui/resources/static/css/components/dialog.css",
            "/client/gui/resources/static/css/components/avatar.css",
            "/client/gui/resources/static/css/components/spinner.css",
            "/client/gui/resources/static/css/components/visualization.css",
            "/client/gui/resources/static/css/components/accordion.css",
            "/client/gui/resources/static/css/components/badge.css",
            "/client/gui/resources/static/css/components/pagination.css",
            "/client/gui/resources/static/css/components/progress.css",
            "/client/gui/resources/static/css/components/skeleton.css",
            "/client/gui/resources/static/css/components/slider.css",
            "/client/gui/resources/static/css/components/tabs.css",
            "/client/gui/resources/static/css/components/toast.css",
            "/client/gui/resources/static/css/components/tooltip.css",
            "/client/gui/resources/static/css/components/typography.css",
            "/client/gui/resources/static/css/components/breadcrumb.css",

//            Pages css
            "/client/gui/resources/static/css/pages/data-table-page.css",
            "/client/gui/resources/static/css/pages/visualization-page.css",
            "/client/gui/resources/static/css/pages/show-case-page.css"

//            ... update here if add more style in future!
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
