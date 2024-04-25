package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    Ok(200, "OK");

    final int code;
    final String text;

    public String toString() {
        return "%d %s".formatted(code, text);
    }
}
