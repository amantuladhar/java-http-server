package http;

import reader.BufferedInputReaderExt;

import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.IOException;
import server.RouteMappingKey;
import java.util.LinkedHashMap;

@Slf4j
@Getter
@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class Request {
    private final HTTPMethod method;
    private final HTTPVersion version;
    private final String path;
    private final String pattern;
    private final byte[] body;

    @Getter(PRIVATE)
    private final Map<String, String> pathParams;
    @Getter(PRIVATE)
    private final InputStream stream;
    @Getter(PRIVATE)
    private final Map<String, String> headers;

    public String getPathParam(String key) {
        return pathParams.get(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public static Request from(InputStream stream, Set<RouteMappingKey> mappings) throws IOException {
        var builder = Request.builder().stream(stream);
        var br = new BufferedInputReaderExt(stream);

        // Status Line
        var statusLine = parseStatusLine(br);
        builder.method(statusLine.method())
                .version(statusLine.httpVersion())
                .path(statusLine.path());

        var headers = parseHeader(br);
        builder.headers(headers);

        // Path Variables
        mappings.stream()
                .filter(key -> key.method() == statusLine.method())
                .map(key -> pathMatches(key, statusLine.path()))
                .filter(matchResult -> matchResult.match())
                .findFirst()
                .ifPresent(matchResult -> {
                    builder.pattern(matchResult.key().pattern());
                    builder.pathParams(matchResult.routeParams());
                });
        // Request Body
        String contentLength = headers.get(HTTPHeader.ContentLength.getText());
        if (contentLength != null && !contentLength.isBlank()) {
            Integer length = Integer.parseInt(contentLength);
            log.info("Content Length - {}", contentLength);
            byte[] body = br.readExact(length);
            builder.body(body);
        }

        return builder.build();
    }

    private static Map<String, String> parseHeader(BufferedInputReaderExt reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        while (true) {
            String line = reader.readLine();
            if (line.isBlank()) {
                break;
            }
            String[] split = line.split(": ");
            headers.put(split[0], split[1]);
        }
        return headers;
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

    private static PathMatchResult pathMatches(RouteMappingKey key, String reqPath) {
        List<String> grpNames = new ArrayList<>();

        // Can't use this pattern for now because test sends / on payload
        var splitPath = key.pattern().split("/");
        var patternStr = Arrays.stream(splitPath)
                .map((part) -> {
                    if (!part.startsWith(":")) {
                        return part;
                    }
                    var grpName = part.substring(1);
                    grpNames.add(grpName);
                    // return "(?<%s>[\\w]*[^\\/])".formatted(grpName);
                    return "(?<%s>.*)".formatted(grpName);
                })
                .collect(joining("\\/"));

        // Remove traling slash if exists
        var path = reqPath.endsWith("/")
                ? reqPath.substring(0, reqPath.length() - 1)
                : reqPath;
        var pattern = Pattern.compile(patternStr);
        var matcher = pattern.matcher(path);
        if (!matcher.matches()) {
            return new PathMatchResult(key, false, Map.of());
        }
        Map<String, String> routeParam = new HashMap<>();
        for (int i = 0; i < grpNames.size(); i++) {
            var grpName = grpNames.get(i);
            var grpValue = matcher.group(i + 1);
            routeParam.put(grpName, grpValue);
        }
        return new PathMatchResult(key, true, routeParam);
    }

    static record PathMatchResult(RouteMappingKey key, boolean match, Map<String, String> routeParams) {
    }

    static record StatusLine(HTTPVersion httpVersion, HTTPMethod method, String path) {
    }
}
