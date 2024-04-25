package http;

public enum HTTPVersion {
    HTTP_1_1;

    public String toString() {
        switch (this) {
            case HTTP_1_1:
                return "HTTP/1.1";
        }
        return "";
    }
}
