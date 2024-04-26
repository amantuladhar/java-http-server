package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HTTPHeader {
    ContentLength("Content-Length"),
    ContentType("Content-Type"),
    UserAgent("User-Agent");

    private final String text;
}
