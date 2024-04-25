package http;

import utils.Assert;

public enum HTTPMethod {
    GET, POST, PUT, DELETE;

    public static HTTPMethod fromStr(String method) {
        Assert.notNull(method, "Expected method to be not null");
        return switch (method.toUpperCase()) {
            case "GET" -> HTTPMethod.GET;
            case "POST" -> HTTPMethod.POST;
            case "PUT" -> HTTPMethod.PUT;
            case "DELETE" -> HTTPMethod.DELETE;
            default -> throw new IllegalArgumentException("Invalid method found: [%s]".formatted(method));
        };
    }
}
