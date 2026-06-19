package main.java.client.gui.components.date_picker;

import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * UiDatePicker — native JavaFX DatePicker with styling (shadcn DatePicker).
 * For a custom calendar popup, combine with UiCalendar + UiPopover.
 *
 * Usage:
 *   UiDatePicker dp = new UiDatePicker();
 *   dp.setOnDateChange(date -> System.out.println(date));
 *   dp.setDate(LocalDate.now());
 */
public class UiDatePicker extends DatePicker {
    public UiDatePicker() {
        getStyleClass().add("ui-date-picker");
        setMaxWidth(Double.MAX_VALUE);
    }

    public UiDatePicker(LocalDate initial) {
        this();
        setValue(initial);
    }

    public void setOnDateChange(Consumer<LocalDate> cb) {
        valueProperty().addListener((obs, o, n) -> { if (n != null && cb != null) cb.accept(n); });
    }

    public LocalDate getDate() { return getValue(); }
    public void setDate(LocalDate d) { setValue(d); }
}
