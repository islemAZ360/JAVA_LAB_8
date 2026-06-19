package main.java.client.gui.components.chart;

import java.util.List;

/**
 * ChartData — data container for UiChart.
 *
 * Usage:
 *   ChartData data = new ChartData("Revenue",
 *       List.of("Jan","Feb","Mar"),
 *       List.of(1200.0, 1500.0, 980.0));
 */
public record ChartData(String seriesName, List<String> labels, List<Double> values) {
    public static ChartData of(String name, List<String> labels, List<Double> values) {
        return new ChartData(name, labels, values);
    }
}
