import http.ContentType;
import http.HTTPHeader;
import http.Response;
import http.StatusCode;
import lombok.extern.slf4j.Slf4j;
import server.HttpServer;
import java.util.Map;

@Slf4j
public class Main {
  public static void main(String[] args) {
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
        .build()
        .start(4221);
  }
}
