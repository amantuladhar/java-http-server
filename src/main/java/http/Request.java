package http;

import reader.BufferedInputReaderExt;

import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

@Getter
@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class Request {
    @Getter(PRIVATE)
    private final InputStream stream;
    private final HTTPMethod method;
    private final HTTPVersion version;
    private final String path;

    public static Request from(InputStream stream) throws IOException {
        var builder = Request.builder().stream(stream);
        var br = new BufferedInputReaderExt(stream);
        var statusLine = parseStatusLine(br);
        builder.method(statusLine.method())
                .version(statusLine.httpVersion())
                .path(statusLine.path());
        return builder.build();
    }

    private static StatusLine parseStatusLine(BufferedInputReaderExt reader) {
        try {
            var statusLine = reader.readLine();
            var parts = statusLine.split(" ");
            var httpMethod = HTTPMethod.fromStr(parts[0]);
            var path = parts[1];
            var httpVersion = HTTPVersion.fromStr(parts[2]);
            return new StatusLine(httpVersion, httpMethod, path);
        } catch (Exception e) {
            throw new HttpRequestParseException("Unable to parse status line for HTTP reqeust", e);
        }
    }

    static record StatusLine(HTTPVersion httpVersion, HTTPMethod method, String path) {
    }
}
