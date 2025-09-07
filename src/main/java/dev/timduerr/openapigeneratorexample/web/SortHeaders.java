package dev.timduerr.openapigeneratorexample.web;

/**
 * SortHeaders.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public enum SortHeaders {

    X_SORT("X-Sort"),
    X_SORT_DIR("X-Sort-Dir");

    private final String value;

    SortHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
