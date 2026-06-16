package client.ui.template.core;

public record DisplayField(String label, String value, FieldRole role) {
    public DisplayField {
        if (label == null) label = "";
        if (value == null) value = "";
        if (role == null) role = FieldRole.META;
    }
}
