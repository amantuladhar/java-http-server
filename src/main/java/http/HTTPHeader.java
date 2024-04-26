package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HTTPHeader {
    ContentLength("Content-Length"),
    ContentType("Content-Type");

    private final String text;
}
