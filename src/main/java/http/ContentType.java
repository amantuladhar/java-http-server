package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    TEXT_PLAN("text/plain");

    private final String text;
}
