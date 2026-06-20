package main.java.client.gui.core;

import javafx.scene.Scene;

import java.net.URL;
import java.util.List;

public final class CssLoader {
    private CssLoader() {}

    private static final List<String> STYLESHEETS = List.of(
//            Global css
            "/main/resources/static/css/global.css",

//            Components css
            "/main/resources/static/css/components/button.css",
            "/main/resources/static/css/components/card.css",
            "/main/resources/static/css/components/alert.css",
            "/main/resources/static/css/components/form.css",
            "/main/resources/static/css/components/data-table.css",
            "/main/resources/static/css/components/sidebar.css",
            "/main/resources/static/css/components/dialog.css",
            "/main/resources/static/css/components/avatar.css",
            "/main/resources/static/css/components/spinner.css",
            "/main/resources/static/css/components/visualization.css",
            "/main/resources/static/css/components/accordion.css",
            "/main/resources/static/css/components/badge.css",
            "/main/resources/static/css/components/pagination.css",
            "/main/resources/static/css/components/progress.css",
            "/main/resources/static/css/components/skeleton.css",
            "/main/resources/static/css/components/slider.css",
            "/main/resources/static/css/components/tabs.css",
            "/main/resources/static/css/components/toast.css",
            "/main/resources/static/css/components/tooltip.css",
            "/main/resources/static/css/components/typography.css",
            "/main/resources/static/css/components/breadcrumb.css",
            "/main/resources/static/css/components/terminal.css",

//            Pages css
            "/main/resources/static/css/pages/data-table-page.css",
            "/main/resources/static/css/pages/visualization-page.css",
            "/main/resources/static/css/pages/show-case-page.css",
            "/main/resources/static/css/pages/browser-page.css",
            "/main/resources/static/css/pages/film-page.css"

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
