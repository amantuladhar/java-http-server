package http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    Ok(200, "OK"), Created(201, "Created"), NotFound(404, "Not Found");

    final int code;
    final String text;

    public String toString() {
        return "%d %s".formatted(code, text);
    }
}
