package http;

public class HttpRequestParseException extends RuntimeException {
    public HttpRequestParseException(Exception e) {
        super(e);
    }

    public HttpRequestParseException(String message, Exception e) {
        super(message, e);
    }

    public HttpRequestParseException(String message) {
        super(message);
    }
}