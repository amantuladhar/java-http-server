package http;

import static utils.Constants.LINE_ENDING;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public record Response(HTTPVersion httpVersion, StatusCode statusCode, Map<String, String> headers, String body) {
    public Response {
        if (headers == null) {
            headers = Map.of();
        }
        if (statusCode == null) {
            statusCode = StatusCode.Ok;
        }
        if (httpVersion == null) {
            httpVersion = HTTPVersion.HTTP_1_1;
        }
    }

    public byte[] toBytes() {
        List<String> respLines = new ArrayList<>();
        respLines.add("%s %s".formatted(httpVersion, statusCode));

        headers.forEach((key, value) -> {
            respLines.add("%s: %s".formatted(key, value));
        });

        if (body != null && !body.isBlank()) {
            log.info("BODY -- {}, Length - {}", body, body.length());
            respLines.add("%s: %s".formatted(HTTPHeader.ContentLength.getText(), body.length()));
            respLines.add("%s%s".formatted(LINE_ENDING, body));
        } else {
            respLines.add(LINE_ENDING);
        }
        String resp = String.join(LINE_ENDING, respLines);
        return resp.getBytes();
    }

    public static ResponseBuilder http1() {
        return Response.builder().httpVersion(HTTPVersion.HTTP_1_1);
    }
}
