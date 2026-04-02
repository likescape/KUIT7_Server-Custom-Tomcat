package http.util;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}