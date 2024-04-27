import http.ContentType;
import http.HTTPHeader;
import http.Response;
import http.StatusCode;
import lombok.extern.slf4j.Slf4j;
import server.HttpServer;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import config.AppConfig;

@Slf4j
public class Main {
  public static void main(String[] args) {
    AppConfig.initialize(args);
    log.info("Logs from your program will appear here!");
    HttpServer.builder()
        .get("/", (req) -> Response.http1().statusCode(StatusCode.Ok).build())
        .get("/echo/:message", (req) -> {
          String message = req.getPathParam("message");
          log.info("Message = {}, Length = {}", message, message.length());
          return Response.http1().statusCode(StatusCode.Ok)
              .headers(Map.of(HTTPHeader.ContentType.getText(), ContentType.TEXT_PLAN.getText()))
              .body(message).build();
        })
        .get("/user-agent", (req) -> {
          String userAgent = req.getHeader(HTTPHeader.UserAgent.getText());
          return Response.builder()
              .headers(Map.of(HTTPHeader.ContentType.getText(), ContentType.TEXT_PLAN.getText()))
              .body(userAgent)
              .build();
        })
        .get("/files/:filename", (req) -> {
          String fileName = req.getPathParam("filename");
          String directory = AppConfig.get("--directory")
              .orElseThrow(() -> new RuntimeException("--directory cli args is not set"));
          try {
            String content = Files.readString(Paths.get(directory + fileName));
            return Response.builder()
                .headers(Map.of(HTTPHeader.ContentType.getText(), ContentType.APPLICATION_OCTET_STREAM.getText()))
                .body(content)
                .build();
          } catch (Exception e) {
            return Response.builder()
                .statusCode(StatusCode.NotFound)
                .build();
          }
        })
        .post("/files/:filename", (req) -> {
          String fileName = req.getPathParam("filename");
          String directory = AppConfig.get("--directory")
              .orElseThrow(() -> new RuntimeException("--directory cli args is not set"));
          byte[] body = req.getBody();
          try {
            Files.write(Paths.get(directory + fileName), body);
            return Response.builder()
                .statusCode(StatusCode.Created)
                .build();
          } catch (IOException e) {
            return Response.builder()
                .statusCode(StatusCode.Created)
                .build();
          }
        })
        .build()
        .start(4221);
  }
}
