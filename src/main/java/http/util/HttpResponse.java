package http.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;
    private final Map<HttpHeader, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    // 공통 헤더 추가 메서드
    public void addHeader(HttpHeader key, String value) {
        headers.put(key, value);
    }

    // 1. Forward: 해당 경로의 파일을 읽어 200 OK로 응답
    // http.util.HttpResponse.java

    public void forward(String url) {
        try {
            File file = new File("./webapp" + url);

            // [수정] 만약 요청한 경로가 디렉토리라면 그 안의 index.html을 찾도록 유도
            if (file.isDirectory()) {
                file = new File(file, "index.html");
            }

            if (!file.exists()) {
                log.warning("File Not Found: " + file.getPath());
                // 여기서 404 응답을 보내는 로직을 추가하면 더 완벽합니다.
                return;
            }

            byte[] body = Files.readAllBytes(file.toPath());

            // 확장자에 따른 Content-Type 설정 (기존 로직)
            String path = file.getName();
            if (path.endsWith(".css")) addHeader(HttpHeader.CONTENT_TYPE, "text/css");
            else if (path.endsWith(".js")) addHeader(HttpHeader.CONTENT_TYPE, "application/javascript");
            else addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");

            addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));

            response200Header();
            responseBody(body);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Forward Error: " + e.getMessage());
        }
    }

    // 2. Redirect: 302 Found 응답과 함께 Location 헤더 전송
    public void redirect(String url) {
        try {
            dos.writeBytes(HttpStatus.FOUND.getStatusLine()); // HTTP/1.1 302 Found
            processHeaders();
            dos.writeBytes(HttpHeader.LOCATION.getName() + ": " + url + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Redirect Error: " + e.getMessage());
        }
    }

    private void response200Header() throws IOException {
        dos.writeBytes(HttpStatus.OK.getStatusLine()); // HTTP/1.1 200 OK
        processHeaders();
        dos.writeBytes("\r\n");
    }

    private void processHeaders() throws IOException {
        for (HttpHeader key : headers.keySet()) {
            dos.writeBytes(key.getName() + ": " + headers.get(key) + "\r\n");
        }
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}