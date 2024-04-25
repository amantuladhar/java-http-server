package http;

import utils.Assert;

public enum HTTPVersion {
    HTTP_1_1;

    public String toString() {
        switch (this) {
            case HTTP_1_1:
                return "HTTP/1.1";
        }
        return "";
    }

    public static HTTPVersion fromStr(String str) {
        Assert.notBlank(str, "HTTPVersion str not valid");
        return switch (str.toUpperCase()) {
            case "HTTP/1.1" -> HTTP_1_1;
            default -> throw new IllegalArgumentException("Invalid httpVersion. [%s]".formatted(str));
        };
    }
}
