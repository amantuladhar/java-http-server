package server;

import static lombok.AccessLevel.PRIVATE;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import http.HTTPMethod;
import http.Request;
import http.Response;
import http.StatusCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public class HttpServer {
    private final Map<MappingKey, HttpRequestProcessor> mappings;

    public void start(int port) {
        try (var ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            try (var cs = ss.accept()) {
                var req = Request.from(cs.getInputStream());
                var resp = process(req);
                var os = cs.getOutputStream();
                os.write(resp.toBytes());
                os.flush();
            }
        } catch (Exception e) {
            log.error("Unknown error: " + e.getMessage(), e);
        }
    }

    private Response process(Request req) {
        var key = new MappingKey(req.getMethod(), req.getPath());
        var processor = mappings.getOrDefault(key, request -> Response.http1()
                .statusCode(StatusCode.NotFound)
                .build());
        return processor.process(req);
    }

    public static HttpServerBuilder builder() {
        return new HttpServerBuilder();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class HttpServerBuilder {
        private HashMap<MappingKey, HttpRequestProcessor> mappings = new HashMap<>();

        public HttpServerBuilder get(String location, HttpRequestProcessor processor) {
            mappings.put(new MappingKey(HTTPMethod.GET, location), processor);
            return this;
        }

        public HttpServerBuilder pos(String location, HttpRequestProcessor processor) {
            mappings.put(new MappingKey(HTTPMethod.POST, location), processor);
            return this;
        }

        public HttpServer build() {
            return new HttpServer(mappings);
        }
    }

    static record MappingKey(HTTPMethod method, String path) {
    }
}
