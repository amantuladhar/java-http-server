import static utils.Constants.NEW_LINE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpResponse;

import http.Response;
import http.StatusCode;
import lombok.extern.slf4j.Slf4j;
import reader.BufferedInputReaderExt;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("Logs from your program will appear here!");

    try (var ss = new ServerSocket(4221)) {
      ss.setReuseAddress(true);
      try (var cs = ss.accept();
          var br = new BufferedInputReaderExt(cs.getInputStream())) {
        // while (!br.isEofReached()) {
        // byte[] content = br.readUntil(NEW_LINE).content();
        // log.info(new String(content));
        // }
        log.info("Read all response");
        var response = Response.http1()
            .statusCode(StatusCode.Ok)
            .build();
        var os = cs.getOutputStream();
        os.write(response.toBytes());
        os.flush();
      }
    } catch (Exception e) {
      log.error("Unknown error: " + e.getMessage(), e);
    }
  }
}
