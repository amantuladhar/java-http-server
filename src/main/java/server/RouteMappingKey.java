package server;

import http.HTTPMethod;

public record RouteMappingKey(HTTPMethod method, String pattern) {
}