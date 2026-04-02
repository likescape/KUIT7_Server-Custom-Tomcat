package http.util;

import model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final Map<HttpHeader, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    // 정적 팩토리 메서드
    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    private HttpRequest(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) throw new IOException("Empty Request");
        this.startLine = new HttpStartLine(line);

        while (!(line = br.readLine()).isEmpty()) {
            parseHeader(line);
        }

        parseParameters(br);
    }

    private void parseHeader(String line) {
        String[] pair = line.split(": ");
        for (HttpHeader h : HttpHeader.values()) {
            if (h.getName().equalsIgnoreCase(pair[0]) && pair.length > 1) {
                headers.put(h, pair[1]);
                break;
            }
        }
    }

    private void parseParameters(BufferedReader br) throws IOException {
        if (startLine.getMethod() == HttpMethod.GET) {
            this.params = HttpRequestUtils.parseQueryParameter(startLine.getQueryString());
        }

        if (startLine.getMethod() == HttpMethod.POST) {
            String lengthStr = headers.get(HttpHeader.CONTENT_LENGTH);
            if (lengthStr != null) {
                String bodyData = IOUtils.readData(br, Integer.parseInt(lengthStr));
                this.params = HttpRequestUtils.parseQueryParameter(bodyData);
            }
        }
    }

    // Getter 메서드들

    public String getPath() { return startLine.getPath(); } // 순수 경로만 반환

    public String getParameter(String name) { return params.get(name); }


    public boolean isLogined() {
        String cookie = headers.get(HttpHeader.COOKIE);
        return cookie != null && cookie.contains("logined=true");
    }
}