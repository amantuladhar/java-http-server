package server;

import http.Request;
import http.Response;

@FunctionalInterface
public interface HttpRequestProcessor {
    Response process(Request req);
}
