package client.gui.components.datatable;

import java.util.Comparator;
import java.util.function.Function;

public record ColumnSpec<T, R>(String title, Function<T, R> accessor, double width, Comparator<T> comparator) {
    public ColumnSpec(String title, Function<T, R> accessor, double width) {
        this(title, accessor, width, Comparator.comparing(t -> {
            R value = accessor.apply(t);
            return value == null ? "" : value.toString();
        }));
    }
}
