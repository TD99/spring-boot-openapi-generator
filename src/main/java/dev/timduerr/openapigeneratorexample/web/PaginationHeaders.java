package dev.timduerr.openapigeneratorexample.web;

/**
 * PaginationHeaders.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public enum PaginationHeaders {

    X_PAGE("X-Page"),
    X_SIZE("X-Size"),
    X_TOTAL_ELEMENTS("X-Total-Elements"),
    X_TOTAL_PAGES("X-Total-Pages");

    private final String value;

    PaginationHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
