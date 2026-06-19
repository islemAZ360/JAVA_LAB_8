package main.java.client.gui.components.chart;

import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import java.util.List;

/**
 * UiChart — multi-type chart wrapper (shadcn Chart).
 * Wraps JavaFX built-in charts with unified API.
 *
 * Usage:
 *   ChartData data = ChartData.of("Sales",
 *       List.of("Q1","Q2","Q3","Q4"),
 *       List.of(4200.0, 5800.0, 3900.0, 7100.0));
 *
 *   UiChart chart = new UiChart(ChartType.BAR, data);
 *   UiChart pie   = new UiChart(ChartType.PIE, data);
 *   UiChart line  = new UiChart(ChartType.LINE, data);
 */
public class UiChart extends StackPane {

    public UiChart(ChartType type, ChartData data) {
        getStyleClass().add("ui-chart");
        getChildren().add(buildChart(type, data));
    }

    public UiChart(ChartType type, List<ChartData> series) {
        getStyleClass().add("ui-chart");
        getChildren().add(buildMultiSeries(type, series));
    }

    private Node buildChart(ChartType type, ChartData d) {
        return switch (type) {
            case PIE   -> buildPie(d);
            case BAR   -> buildBar(List.of(d));
            case LINE  -> buildLine(List.of(d));
            case AREA  -> buildArea(List.of(d));
        };
    }

    private Node buildMultiSeries(ChartType type, List<ChartData> series) {
        return switch (type) {
            case PIE  -> buildPie(series.get(0));
            case BAR  -> buildBar(series);
            case LINE -> buildLine(series);
            case AREA -> buildArea(series);
        };
    }

    private PieChart buildPie(ChartData d) {
        PieChart pie = new PieChart();
        pie.getStyleClass().add("ui-chart-pie");
        for (int i = 0; i < d.labels().size(); i++) {
            pie.getData().add(new PieChart.Data(d.labels().get(i), d.values().get(i)));
        }
        return pie;
    }

    private BarChart<String, Number> buildBar(List<ChartData> series) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.getStyleClass().add("ui-chart-bar");
        chart.setAnimated(true);
        for (ChartData d : series) {
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName(d.seriesName());
            for (int i = 0; i < d.labels().size(); i++)
                s.getData().add(new XYChart.Data<>(d.labels().get(i), d.values().get(i)));
            chart.getData().add(s);
        }
        return chart;
    }

    private LineChart<String, Number> buildLine(List<ChartData> series) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.getStyleClass().add("ui-chart-line");
        chart.setAnimated(true);
        for (ChartData d : series) {
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName(d.seriesName());
            for (int i = 0; i < d.labels().size(); i++)
                s.getData().add(new XYChart.Data<>(d.labels().get(i), d.values().get(i)));
            chart.getData().add(s);
        }
        return chart;
    }

    private AreaChart<String, Number> buildArea(List<ChartData> series) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.getStyleClass().add("ui-chart-area");
        chart.setAnimated(true);
        for (ChartData d : series) {
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName(d.seriesName());
            for (int i = 0; i < d.labels().size(); i++)
                s.getData().add(new XYChart.Data<>(d.labels().get(i), d.values().get(i)));
            chart.getData().add(s);
        }
        return chart;
    }
}
