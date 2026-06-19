package main.java.client.gui.components.sonner;

import javafx.stage.Window;
import main.java.client.gui.components.toast.ToastVariant;
import main.java.client.gui.components.toast.UiToast;

/**
 * UiSonner — opinionated toast stack (shadcn Sonner).
 * Static API matches Sonner's toast.success / toast.error pattern.
 *
 * Usage:
 *   UiSonner.init(stage);
 *   UiSonner.success("Saved!");
 *   UiSonner.error("Something went wrong");
 *   UiSonner.promise("Uploading...", "Done!", "Failed");
 */
public class UiSonner {
    private static Window owner;

    public static void init(Window window) { owner = window; }

    public static void success(String msg)  { fire(null, msg, ToastVariant.SUCCESS, 3000); }
    public static void error(String msg)    { fire(null, msg, ToastVariant.ERROR,   4000); }
    public static void warning(String msg)  { fire(null, msg, ToastVariant.WARNING, 3500); }
    public static void info(String msg)     { fire(null, msg, ToastVariant.INFO,    3000); }
    public static void message(String msg)  { fire(null, msg, ToastVariant.DEFAULT, 3000); }

    public static void success(String title, String msg) { fire(title, msg, ToastVariant.SUCCESS, 3000); }
    public static void error(String title, String msg)   { fire(title, msg, ToastVariant.ERROR,   4000); }

    /** Shows "loading", runs task, then shows success or error. */
    public static void promise(String loading, String success, String error, Runnable task) {
        fire(null, loading, ToastVariant.INFO, 500);
        new Thread(() -> {
            try {
                task.run();
                javafx.application.Platform.runLater(() -> fire(null, success, ToastVariant.SUCCESS, 3000));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> fire(null, error + ": " + e.getMessage(), ToastVariant.ERROR, 4000));
            }
        }).start();
    }

    private static void fire(String title, String msg, ToastVariant v, long ms) {
        if (owner == null) { System.err.println("[UiSonner] Call UiSonner.init(stage) first"); return; }
        UiToast.show(owner, title, msg, v, ms);
    }
}
