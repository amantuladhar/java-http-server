package http;

import static utils.Constants.LINE_ENDING;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;

@Builder
public record Response(HTTPVersion httpVersion, StatusCode statusCode) {

    public byte[] toBytes() {
        List<String> respLines = new ArrayList<>();
        respLines.add("%s %s".formatted(httpVersion, statusCode));

        // This is EOF LINE_ENDING
        respLines.add(LINE_ENDING);
        String resp = String.join(LINE_ENDING, respLines);
        return resp.getBytes();
    }

    public static ResponseBuilder http1() {
        return Response.builder().httpVersion(HTTPVersion.HTTP_1_1);
    }
}
