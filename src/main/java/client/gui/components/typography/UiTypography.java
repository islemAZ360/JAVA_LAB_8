package main.java.client.gui.components.typography;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * UiTypography — semantic text elements (shadcn Typography).
 *
 * Usage:
 *   UiTypography.h1("Welcome back")
 *   UiTypography.h2("Your Dashboard")
 *   UiTypography.p("Lorem ipsum...")
 *   UiTypography.muted("Last updated 3 minutes ago")
 *   UiTypography.code("npm install")
 *   UiTypography.lead("A modal dialog...")
 *   UiTypography.blockquote("The best UI is no main.java.client.gui.")
 *   UiTypography.inlineCode("className")
 */
public final class UiTypography {
    private UiTypography() {}

    public static Label h1(String text)        { return styled(text, "ui-h1"); }
    public static Label h2(String text)        { return styled(text, "ui-h2"); }
    public static Label h3(String text)        { return styled(text, "ui-h3"); }
    public static Label h4(String text)        { return styled(text, "ui-h4"); }
    public static Label p(String text)         { return styled(text, "ui-p"); }
    public static Label lead(String text)      { return styled(text, "ui-lead"); }
    public static Label large(String text)     { return styled(text, "ui-large"); }
    public static Label small(String text)     { return styled(text, "ui-small"); }
    public static Label muted(String text)     { return styled(text, "ui-muted"); }
    public static Label blockquote(String text){ return styled(text, "ui-blockquote"); }
    public static Label code(String text)      { return styled(text, "ui-code"); }
    public static Label inlineCode(String text){ return styled(text, "ui-inline-code"); }

    private static Label styled(String text, String css) {
        Label lbl = new Label(text == null ? "" : text);
        lbl.getStyleClass().add(css);
        lbl.setWrapText(true);
        return lbl;
    }

    /** Rich text with mixed styles. */
    public static TextFlow rich(Text... segments) {
        TextFlow tf = new TextFlow(segments);
        tf.getStyleClass().add("ui-rich-text");
        return tf;
    }
}
