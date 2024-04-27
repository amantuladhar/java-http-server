package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    TEXT_PLAN("text/plain");

    private final String text;
}
