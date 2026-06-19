package main.java.client.gui.components.skeleton;

import javafx.scene.layout.Region;

/**
 * UiSkeleton — animated loading placeholder (shadcn Skeleton).
 *
 * Usage:
 *   UiSkeleton sk = new UiSkeleton(200, 16);   // width=200 height=16
 *   UiSkeleton sk = UiSkeleton.circle(40);     // circle avatar placeholder
 */
public class UiSkeleton extends Region {
    public UiSkeleton(double width, double height) {
        getStyleClass().add("ui-skeleton");
        setPrefSize(width, height);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    public static UiSkeleton circle(double diameter) {
        UiSkeleton sk = new UiSkeleton(diameter, diameter);
        sk.getStyleClass().add("ui-skeleton-circle");
        sk.setStyle("-fx-background-radius: 50%;");
        return sk;
    }

    public static UiSkeleton text(double width) {
        UiSkeleton sk = new UiSkeleton(width, 14);
        sk.getStyleClass().add("ui-skeleton-text");
        return sk;
    }
}
