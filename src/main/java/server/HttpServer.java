package server;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final ExecutorService executorService;

    public void start(int port) {
        try (var ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            handleConnection(ss);
        } catch (Exception e) {
            log.error("Unknown error: " + e.getMessage(), e);
        }
    }

    private void handleConnection(ServerSocket ss) throws IOException {
        while (true) {
            log.info("Waiting for client connection....");
            Socket cs = ss.accept();
            executorService.execute(() -> {
                try {
                    var req = Request.from(cs.getInputStream(), mappings.keySet());

                    var key = new RouteMappingKey(req.getMethod(), req.getPattern());
                    var resp = mappings.getOrDefault(key, res -> Response.http1()
                            .statusCode(StatusCode.NotFound)
                            .build())
                            .process(req);

                    var os = cs.getOutputStream();
                    os.write(resp.toBytes());
                    os.flush();
                } catch (IOException e) {
                    log.error("Something is wrong -- ", e);
                } finally {
                    try {
                        cs.close();
                    } catch (IOException e) {
                        log.error("Unable to close client socket.", e);
                    }
                }
            });
        }

    }

    public static HttpServerBuilder builder() {
        return new HttpServerBuilder();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class HttpServerBuilder {
        private HashMap<RouteMappingKey, HttpRequestProcessor> mappings = new HashMap<>();
        private ExecutorService executorService;

        public HttpServerBuilder executorService(ExecutorService es) {
            this.executorService = es;
            return this;
        }

        public HttpServerBuilder get(String location, HttpRequestProcessor processor) {
            mappings.put(new RouteMappingKey(HTTPMethod.GET, location), processor);
            return this;
        }

        public HttpServerBuilder post(String location, HttpRequestProcessor processor) {
            mappings.put(new RouteMappingKey(HTTPMethod.POST, location), processor);
            return this;
        }

        public HttpServer build() {
            if (executorService == null) {
                executorService = Executors.newVirtualThreadPerTaskExecutor();
            }
            return new HttpServer(mappings, executorService);
        }
    }

    static record FindRouterResult(Request req, HttpRequestProcessor processor) {
    }
}
