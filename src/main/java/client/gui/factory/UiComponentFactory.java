package main.java.client.gui.factory;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import main.java.client.gui.components.alert.AlertVariant;
import main.java.client.gui.components.alert.UiAlert;
import main.java.client.gui.components.alert_dialog.UiAlertDialog;
import main.java.client.gui.components.button.ButtonVariant;
import main.java.client.gui.components.button.ComponentSubmitEvent;
import main.java.client.gui.components.button.GatewaySubmitEvent;
import main.java.client.gui.components.button.UiButton;
import main.java.client.gui.components.card.UiCard;
import main.java.client.gui.components.checkbox.UiCheckbox;
import main.java.client.gui.components.input.UiInputGroup;
import main.java.client.gui.components.progress.UiProgress;
import main.java.client.gui.components.radio_group.UiRadioGroup;
import main.java.client.gui.components.select.UiSelect;
import main.java.client.gui.components.slider.UiSlider;
import main.java.client.gui.components.table.UiTable;
import main.java.client.gui.components.tabs.UiTabs;
import main.java.client.gui.components.textarea.UiTextarea;
import main.java.client.gui.components.toast.ToastVariant;
import main.java.client.gui.components.toast.UiToast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UiComponentFactory {

    private static final Logger logger = Logger.getLogger(UiComponentFactory.class.getName());

    private static final List<String> ALLOWED_FILE_ROOTS = List.of(System.getProperty("user.home"));

    public UiComponentFactory() {
    }


    public static Region create(String json, Window owner) {
        return create(json, owner, /* wrapAsGatewayEvent= */ true);
    }

    private static Region create(String json, Window owner, boolean wrapAsGatewayEvent) {
        try {
            String type = extractField(json, "type");
            String props = extractObject(json, "props");

            Region region = switch (type.toLowerCase()) {
                case "alert" -> buildAlert(props);
                case "diff" -> buildDiff(props);
                case "checkbox_group" -> buildCheckboxGroup(props);
                case "input_form" -> buildInputForm(props, owner);
                case "alert_dialog" -> buildAlertDialog(props, owner);
                case "toast" -> buildToast(props, owner);
                case "progress" -> buildProgress(props);
                case "table" -> buildTable(props);
                case "tabs" -> buildTabs(props, owner);
                case "radio_group" -> buildRadioGroup(props);
                case "select" -> buildSelect(props);
                case "slider" -> buildSlider(props);
                case "file_action" -> buildFileAction(props);
                default -> errorAlert("Unknown Component", "Unsupported component type: \"" + type + "\"");
            };

            if (region != null && wrapAsGatewayEvent && !"tabs".equalsIgnoreCase(type) && !Boolean.TRUE.equals(region.getProperties().get("gateway_wrapped"))) {

                region.getProperties().put("gateway_wrapped", Boolean.TRUE);

                region.addEventHandler(ComponentSubmitEvent.SUBMIT_TYPE, event -> {
                    event.consume();

                    GatewaySubmitEvent gatewayEvent = new GatewaySubmitEvent(event.getData());
                    region.fireEvent(gatewayEvent);
                });
            }

            return region;

        } catch (Exception e) {
            e.printStackTrace();
            return errorAlert("Parse Error", "Could not render AI response: " + e.getMessage());
        }
    }


    private static Region buildAlert(String props) {
        String title = extractField(props, "title");
        String message = extractField(props, "message");
        String variant = extractField(props, "variant");

        AlertVariant av = parseEnum(AlertVariant.class, variant, AlertVariant.INFO);
        return new UiAlert(title, message, av);
    }


    private static Region buildDiff(String props) {
        String title = extractField(props, "title");
        List<String> lineObjects = extractArray(props, "lines");

        UiCard card = new UiCard(title, "");

        VBox diffBox = new VBox();
        diffBox.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");

        for (String lineJson : lineObjects) {
            String type = extractField(lineJson, "type");
            String content = extractField(lineJson, "content");

            HBox row = new HBox();
            Label prefix = new Label();
            Label text = new Label(content);
            text.setWrapText(false);
            text.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(text, Priority.ALWAYS);

            switch (type.toLowerCase()) {
                case "added" -> {
                    prefix.setText("+ ");
                    row.setStyle("-fx-background-color: #1a3a1a; -fx-padding: 1 6 1 6;");
                    prefix.setTextFill(Color.web("#4ade80"));
                    text.setTextFill(Color.web("#4ade80"));
                }
                case "removed" -> {
                    prefix.setText("- ");
                    row.setStyle("-fx-background-color: #3a1a1a; -fx-padding: 1 6 1 6;");
                    prefix.setTextFill(Color.web("#f87171"));
                    text.setTextFill(Color.web("#f87171"));
                }
                default -> {
                    prefix.setText("  ");
                    row.setStyle("-fx-padding: 1 6 1 6;");
                }
            }

            row.getChildren().addAll(prefix, text);
            diffBox.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(diffBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        scroll.setStyle("-fx-background-color: #0d1117;");

        card.content().getChildren().add(scroll);
        return card;
    }

    private static Region buildCheckboxGroup(String props) {
        String title = extractField(props, "title");
        String submitLabel = extractField(props, "submit_label");
        List<String> options = extractArray(props, "options");

        UiCard card = new UiCard(title, "");
        VBox group = new VBox(8);
        List<UiCheckbox> boxes = new ArrayList<>();

        for (String optJson : options) {
            String id = extractField(optJson, "id");
            String label = extractField(optJson, "label");
            boolean checked = "true".equalsIgnoreCase(extractField(optJson, "checked"));

            UiCheckbox cb = new UiCheckbox(label);
            cb.setSelected(checked);
            cb.setUserData(id);
            boxes.add(cb);
            group.getChildren().add(cb);
        }

        UiButton submit = new UiButton(submitLabel.isEmpty() ? "Submit" : submitLabel, ButtonVariant.DEFAULT);

        submit.setupDynamicSubmit(() -> {
            List<String> selected = boxes.stream().filter(UiCheckbox::isSelected).map(cb -> (String) cb.getUserData()).toList();

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("checkbox_selections", selected);

            return data;
        });

        card.setUserData((Supplier<Map<String, Object>>) () -> {
            List<String> selected = boxes.stream().filter(UiCheckbox::isSelected).map(cb -> (String) cb.getUserData()).toList();
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("checkbox_selections", selected);
            return data;
        });

        card.content().getChildren().addAll(group, submit);
        return card;
    }

    public static Region buildInputForm(String props, Window owner) {
        return buildInputForm(props, owner, null);
    }

    public static Region buildInputForm(String props, Window owner, Consumer<Map<String, String>> onSubmit) {
        String title = extractField(props, "title");
        String submitLabel = extractField(props, "submit_label");
        List<String> fields = extractArray(props, "fields");

        UiCard card = new UiCard(title, "");
        VBox form = new VBox(10);
        Map<String, Control> fieldMap = new LinkedHashMap<>();

        for (String fieldJson : fields) {
            String id = extractField(fieldJson, "id");
            String label = extractField(fieldJson, "label");
            String placeholder = extractField(fieldJson, "placeholder");
            String type = extractField(fieldJson, "type");

            Label lbl = new Label(label);
            Control input = switch (type.toLowerCase()) {
                case "textarea" -> new UiTextarea(placeholder);
                case "password" -> {
                    PasswordField pf = new PasswordField();
                    pf.setPromptText(placeholder);
                    yield pf;
                }
                default -> {
                    UiInputGroup inputGroup = new UiInputGroup(placeholder);
                    yield inputGroup.input();
                }
            };

            fieldMap.put(id, input);
            form.getChildren().addAll(lbl, input);
        }

        UiButton submit = new UiButton(submitLabel.isEmpty() ? "Submit" : submitLabel, ButtonVariant.DEFAULT);

        submit.setupDynamicSubmit(() -> {
            Map<String, Object> values = new LinkedHashMap<>();
            fieldMap.forEach((id, control) -> {
                String value = switch (control) {
                    case PasswordField pf -> pf.getText();
                    case TextField ig -> ig.getText();
                    case TextArea ta -> ta.getText();
                    default -> "";
                };
                values.put(id, value);
            });

            if (onSubmit != null) {
                Map<String, String> stringValues = new LinkedHashMap<>();
                values.forEach((k, v) -> stringValues.put(k, String.valueOf(v)));

                onSubmit.accept(stringValues);

                return values;
            } else {
                return values;
            }
        });

        card.setUserData((Supplier<Map<String, Object>>) () -> {
            Map<String, Object> values = new java.util.LinkedHashMap<>();
            fieldMap.forEach((id, control) -> {
                String value = switch (control) {
                    case PasswordField pf -> pf.getText();
                    case TextField tf -> tf.getText();
                    case TextArea ta -> ta.getText();
                    default -> "";
                };
                values.put(id, value);
            });
            return values;
        });

        card.content().getChildren().addAll(form, submit);
        return card;
    }


    private static Region buildAlertDialog(String props, Window owner) {
        String title = extractField(props, "title");
        String message = extractField(props, "message");
        String confirmLabel = extractField(props, "confirm_label");

        UiCard card = new UiCard(title, message);
        UiButton trigger = new UiButton(confirmLabel.isEmpty() ? "Confirm" : confirmLabel, ButtonVariant.DESTRUCTIVE);

        trigger.setOnAction(e -> UiAlertDialog.confirm(title, message, () -> logger.info("Alert dialog confirmed: " + title)));

        card.footer().getChildren().add(trigger);
        return card;
    }


    private static Region buildToast(String props, Window owner) {
        String title = extractField(props, "title");
        String message = extractField(props, "message");
        String variant = extractField(props, "variant");
        String durStr = extractField(props, "duration_ms");

        long duration = durStr.isEmpty() ? 3000 : parseLong(durStr, 3000);
        ToastVariant tv = parseEnum(ToastVariant.class, variant, ToastVariant.DEFAULT);

        if (owner != null) {
            Platform.runLater(() -> UiToast.show(owner, title, message, tv, duration));
        }

        return new UiAlert(title, message, parseEnum(AlertVariant.class, variant, AlertVariant.INFO));
    }

    private static Region buildProgress(String props) {
        String label = extractField(props, "label");
        String valueStr = extractField(props, "value");
        String indetermStr = extractField(props, "indeterminate");
        boolean indeterminate = "true".equalsIgnoreCase(indetermStr);

        UiProgress progress = new UiProgress();
        progress.setLabel(label);

        if (indeterminate) {
            progress.setIndeterminate();
        } else {
            double value = parseDouble(valueStr, 0.0);
            progress.setProgress(value);
        }

        return progress;
    }


    private static Region buildTable(String props) {
        List<String> columnDefs = extractArray(props, "columns");
        List<String> rowDefs = extractArray(props, "rows");

        record ColMeta(String id, String label, double width) {
        }
        List<ColMeta> columns = columnDefs.stream().map(col -> new ColMeta(extractField(col, "id"), extractField(col, "label"), parseDouble(extractField(col, "width"), 120))).toList();

        List<Map<String, String>> rows = rowDefs.stream().map(row -> {
            Map<String, String> map = new LinkedHashMap<>();
            for (ColMeta col : columns) {
                map.put(col.id(), extractField(row, col.id()));
            }
            return map;
        }).toList();

        UiTable<Map<String, String>> table = new UiTable<>();
        for (ColMeta col : columns) {
            table.addColumn(col.label(), r -> r.getOrDefault(col.id(), ""), col.width());
        }
        table.setItems(rows);
        table.setPrefHeight(Math.min(400, 40 + rows.size() * 35.0));

        return table;
    }


    private static Region buildTabs(String props, Window owner) {
        List<String> tabDefs = extractArray(props, "tabs");
        UiTabs tabs = new UiTabs();
        int totalTabs = tabDefs.size();

        Map<String, Object> globalFormData = new java.util.LinkedHashMap<>();

        List<Region> innerRegions = new ArrayList<>();

        for (int i = 0; i < totalTabs; i++) {
            String tabJson = tabDefs.get(i);
            String label = extractField(tabJson, "label");
            String contentType = extractField(tabJson, "content_type");
            String contentObj = extractObject(tabJson, "content");

            String innerJson = "{\"type\":\"" + contentType + "\",\"props\":" + contentObj + "}";
            Region inner = create(innerJson, owner, /* wrapAsGatewayEvent= */ false);
            innerRegions.add(inner);

            inner.addEventHandler(ComponentSubmitEvent.SUBMIT_TYPE, event -> {
                event.consume();
                globalFormData.putAll(event.getData());

                int current = tabs.getSelectionModel().getSelectedIndex();
                if (current < totalTabs - 1) {
                    tabs.getSelectionModel().selectNext();
                } else {
                    tabs.fireEvent(new GatewaySubmitEvent(globalFormData));
                }
            });

            tabs.addTab(label, inner);
        }

        tabs.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int leaving = oldVal.intValue();
            if (leaving < 0 || leaving >= innerRegions.size()) return;

            Object ud = innerRegions.get(leaving).getUserData();
            if (ud instanceof Supplier<?> supplier) {
                @SuppressWarnings("unchecked") Map<String, Object> liveData = (Map<String, Object>) supplier.get();
                globalFormData.putAll(liveData);
            }
        });

        return tabs;
    }

    private static Region buildRadioGroup(String props) {
        String title = extractField(props, "title");
        String submitLabel = extractField(props, "submit_label");
        List<String> options = extractArrayStrings(props, "options");

        UiCard card = new UiCard(title, "");
        UiRadioGroup<String> rg = new UiRadioGroup<>(options);
        rg.selectFirst();

        UiButton submit = new UiButton(submitLabel.isEmpty() ? "Submit" : submitLabel, ButtonVariant.DEFAULT);

        submit.setupDynamicSubmit(() -> {
            String selected = rg.selected();

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("radio_selection", selected != null ? selected : "");

            return data;
        });

        card.setUserData((Supplier<Map<String, Object>>) () -> {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("radio_selection", rg.selected() != null ? rg.selected() : "");
            return data;
        });

        card.content().getChildren().addAll(rg, submit);
        return card;
    }

    private static Region buildSelect(String props) {
        String label = extractField(props, "label");
        String defaultVal = extractField(props, "default");
        String submitLabel = extractField(props, "submit_label");
        List<String> options = extractArrayStrings(props, "options");

        UiCard card = new UiCard(label, "");
        UiSelect<String> select = new UiSelect<>(options);
        if (!defaultVal.isEmpty()) {
            select.getSelectionModel().select(defaultVal);
        } else {
            select.selectFirstIfAny();
        }

        UiButton submit = new UiButton(submitLabel.isEmpty() ? "Confirm" : submitLabel, ButtonVariant.DEFAULT);

        submit.setupDynamicSubmit(() -> {
            String chosen = select.selected();

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("select_selection", chosen != null ? chosen : "");

            return data;
        });

        card.setUserData((Supplier<Map<String, Object>>) () -> {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("select_selection", select.selected() != null ? select.selected() : "");
            return data;
        });

        card.content().getChildren().addAll(select, submit);
        return card;
    }


    private static Region buildSlider(String props) {
        String label = extractField(props, "label");
        String submitLabel = extractField(props, "submit_label");
        double min = parseDouble(extractField(props, "min"), 0);
        double max = parseDouble(extractField(props, "max"), 100);
        double value = parseDouble(extractField(props, "value"), 50);

        UiCard card = new UiCard(label, "");
        UiSlider slider = new UiSlider(min, max, value, label);

        UiButton apply = new UiButton(submitLabel.isEmpty() ? "Apply" : submitLabel, ButtonVariant.DEFAULT);

        apply.setupDynamicSubmit(() -> {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("slider_value", (int) slider.getValue());

            return data;
        });

        card.setUserData((Supplier<Map<String, Object>>) () -> {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("slider_value", (int) slider.getValue());
            return data;
        });

        card.content().getChildren().addAll(slider, apply);
        return card;
    }

    private static Region buildFileAction(String props) {
        String action = extractField(props, "action");
        String path = extractField(props, "path");
        String encoding = extractField(props, "encoding");
        if (encoding.isEmpty()) encoding = "UTF-8";

        if (!isPathAllowed(path)) {
            return errorAlert("Access Denied", "File path is outside the allowed directory: " + path);
        }

        try {
            if ("read".equalsIgnoreCase(action)) {
                String content = Files.readString(Path.of(path), Charset.forName(encoding));
                return new UiAlert("File: " + Path.of(path).getFileName(), content, AlertVariant.INFO);

            } else if ("write".equalsIgnoreCase(action)) {
                String content = extractField(props, "content");
                boolean append = "true".equalsIgnoreCase(extractField(props, "append"));

                if (append) {
                    Files.writeString(Path.of(path), content, Charset.forName(encoding), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                } else {
                    Files.writeString(Path.of(path), content, Charset.forName(encoding), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                }
                return new UiAlert("File Saved", "Written to: " + path, AlertVariant.SUCCESS);

            } else {
                return errorAlert("Unknown Action", "Unsupported file action: " + action);
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "File I/O error", e);
            return errorAlert("File Error", e.getMessage());
        }
    }


    private static boolean isPathAllowed(String path) {
        try {
            Path normalized = Path.of(path).toAbsolutePath().normalize();
            return ALLOWED_FILE_ROOTS.stream().anyMatch(root -> normalized.startsWith(Path.of(root).toAbsolutePath().normalize()));
        } catch (Exception e) {
            return false;
        }
    }


    private static UiAlert errorAlert(String title, String message) {
        return new UiAlert(title, message, AlertVariant.ERROR);
    }


    public static String extractField(String json, String field) {
        if (json == null || json.isBlank()) return "";
        String key = "\"" + field + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx == -1) return "";

        int colon = json.indexOf(":", keyIdx + key.length());
        if (colon == -1) return "";

        int pos = colon + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;

        if (pos >= json.length()) return "";

        char first = json.charAt(pos);

        if (first == '"') {
            StringBuilder sb = new StringBuilder();
            pos++;
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (c == '\\' && pos + 1 < json.length()) {
                    char next = json.charAt(pos + 1);
                    sb.append(switch (next) {
                        case '"' -> '"';
                        case '\\' -> '\\';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        default -> next;
                    });
                    pos += 2;
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                    pos++;
                }
            }
            return sb.toString();
        }

        int end = pos;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(pos, end).trim();
    }

    static String extractObject(String json, String field) {
        if (json == null) return "{}";
        String key = "\"" + field + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx == -1) return "{}";

        int colon = json.indexOf(":", keyIdx + key.length());
        if (colon == -1) return "{}";

        int pos = colon + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;

        if (pos >= json.length() || json.charAt(pos) != '{') return "{}";

        int start = pos;
        int depth = 0;
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return json.substring(start, pos + 1);
            }
            pos++;
        }
        return "{}";
    }


    static List<String> extractArray(String json, String field) {
        if (json == null) return List.of();
        String key = "\"" + field + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx == -1) return List.of();

        int colon = json.indexOf(":", keyIdx + key.length());
        if (colon == -1) return List.of();

        int pos = colon + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        if (pos >= json.length() || json.charAt(pos) != '[') return List.of();

        pos++;
        List<String> result = new ArrayList<>();
        int depth = 0;
        int itemStart = -1;
        boolean inString = false;

        while (pos < json.length()) {
            char c = json.charAt(pos);

            if (inString) {
                if (c == '\\') {
                    pos += 2;
                    continue;
                }
                if (c == '"') inString = false;
                pos++;
                continue;
            }

            if (c == '"') {
                if (depth == 0 && itemStart == -1) {
                    itemStart = pos;
                }
                inString = true;
                pos++;
                continue;
            }

            if (c == '{' || c == '[') {
                if (depth == 0) itemStart = pos;
                depth++;
            } else if (c == '}' || c == ']') {
                if (c == ']' && depth == 0) break;

                depth--;
                if (depth == 0 && itemStart != -1) {
                    result.add(json.substring(itemStart, pos + 1));
                    itemStart = -1;
                }
            } else if (c == ',' && depth == 0) {
                if (itemStart != -1) {
                    result.add(json.substring(itemStart, pos).trim().replaceAll("^\"|\"$", ""));
                    itemStart = -1;
                }
            }

            pos++;
        }

        if (itemStart != -1 && itemStart < pos) {
            String last = json.substring(itemStart, pos).trim().replaceAll("^\"|\"$", "");
            if (!last.isEmpty()) result.add(last);
        }

        return result;
    }

    static List<String> extractArrayStrings(String json, String field) {
        return extractArray(json, field);
    }


    private static double parseDouble(String s, double fallback) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static long parseLong(String s, long fallback) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> cls, String value, E fallback) {
        try {
            return Enum.valueOf(cls, value.trim().toUpperCase());
        } catch (Exception e) {
            return fallback;
        }
    }
}
