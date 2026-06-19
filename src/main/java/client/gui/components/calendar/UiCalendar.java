package main.java.client.gui.components.calendar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.UiButton;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

public class UiCalendar extends VBox {
    private YearMonth viewMonth;
    private LocalDate selected;
    private Consumer<LocalDate> onSelect;

    public UiCalendar() {
        this(YearMonth.now());
    }

    public UiCalendar(YearMonth initialMonth) {
        getStyleClass().add("ui-calendar");
        setSpacing(8);
        setPadding(new Insets(12));
        this.viewMonth = initialMonth;
        build();
    }

    public void setOnDateSelect(Consumer<LocalDate> cb) { this.onSelect = cb; }

    public void setSelectedDate(LocalDate date) {
        this.selected = date;
        if (date != null) viewMonth = YearMonth.from(date);
        build();
    }

    public LocalDate getSelectedDate() { return selected; }

    private void build() {
        getChildren().clear();

        UiButton prev = new UiButton("‹", ButtonVariant.GHOST);
        UiButton next = new UiButton("›", ButtonVariant.GHOST);
        Label monthLabel = new Label(viewMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + viewMonth.getYear());
        monthLabel.getStyleClass().add("ui-calendar-header");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox header = new HBox(6, prev, sp, monthLabel, sp, next);
        header.setAlignment(Pos.CENTER);
        prev.setOnAction(e -> { viewMonth = viewMonth.minusMonths(1); build(); });
        next.setOnAction(e -> { viewMonth = viewMonth.plusMonths(1); build(); });
        getChildren().add(header);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("ui-calendar-grid");
        grid.setHgap(2); grid.setVgap(2);
        String[] days = {"Mo","Tu","We","Th","Fr","Sa","Su"};
        for (int i = 0; i < 7; i++) {
            Label d = new Label(days[i]);
            d.getStyleClass().add("ui-calendar-dow");
            d.setAlignment(Pos.CENTER); d.setPrefWidth(34);
            grid.add(d, i, 0);
        }

        LocalDate first = viewMonth.atDay(1);
        int startCol = (first.getDayOfWeek().getValue() - 1) % 7;
        int daysInMonth = viewMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = viewMonth.atDay(day);
            int col = (startCol + day - 1) % 7;
            int row = (startCol + day - 1) / 7 + 1;

            UiButton btn = new UiButton(String.valueOf(day), ButtonVariant.GHOST);
            btn.getStyleClass().add("ui-calendar-day");
            btn.setPrefWidth(34);
            if (date.equals(today)) btn.getStyleClass().add("ui-calendar-today");
            if (date.equals(selected)) btn.applyVariant(ButtonVariant.DEFAULT);
            btn.setOnAction(e -> {
                selected = date;
                if (onSelect != null) onSelect.accept(date);
                build();
            });
            grid.add(btn, col, row);
        }
        getChildren().add(grid);
    }
}
