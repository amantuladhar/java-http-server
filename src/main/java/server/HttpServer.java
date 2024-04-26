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
    private final Map<RouteMappingKey, HttpRequestProcessor> mappings;

    public void start(int port) {
        try (var ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            while (true) {
                try (var cs = ss.accept()) {
                    var req = Request.from(cs.getInputStream(), mappings.keySet());

                    var key = new RouteMappingKey(req.getMethod(), req.getPattern());
                    var resp = mappings.getOrDefault(key, res -> Response.http1()
                            .statusCode(StatusCode.NotFound)
                            .build())
                            .process(req);

                    var os = cs.getOutputStream();
                    os.write(resp.toBytes());
                    os.flush();
                }
            }
        } catch (Exception e) {
            log.error("Unknown error: " + e.getMessage(), e);
        }
    }

    public static HttpServerBuilder builder() {
        return new HttpServerBuilder();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class HttpServerBuilder {
        private HashMap<RouteMappingKey, HttpRequestProcessor> mappings = new HashMap<>();

        public HttpServerBuilder get(String location, HttpRequestProcessor processor) {
            mappings.put(new RouteMappingKey(HTTPMethod.GET, location), processor);
            return this;
        }

        public HttpServerBuilder post(String location, HttpRequestProcessor processor) {
            mappings.put(new RouteMappingKey(HTTPMethod.POST, location), processor);
            return this;
        }

        public HttpServer build() {
            return new HttpServer(mappings);
        }
    }

    static record FindRouterResult(Request req, HttpRequestProcessor processor) {
    }
}
