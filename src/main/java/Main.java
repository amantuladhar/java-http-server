import http.Response;
import http.StatusCode;
import lombok.extern.slf4j.Slf4j;
import server.HttpServer;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("Logs from your program will appear here!");
    HttpServer.builder()
        .get("/", (req) -> Response.http1().statusCode(StatusCode.Ok).build())
        .build()
        .start(4221);
  }
}
