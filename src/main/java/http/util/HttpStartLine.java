package http.util;

public class HttpStartLine {
    private final HttpMethod method;
    private final String path;         // 순수 경로 (예: /user/create)
    private final String queryString;  // 데이터 부분 (예: userId=jw...)
    private final String version;

    public HttpStartLine(String line) {
        String[] tokens = line.split(" ");
        this.method = HttpMethod.of(tokens[0]);
        this.version = tokens[2];

        // URL을 경로와 쿼리스트링으로 분리
        String[] urlParts = tokens[1].split("\\?", 2);
        this.path = urlParts[0];
        this.queryString = (urlParts.length > 1) ? urlParts[1] : "";
    }

    public HttpMethod getMethod() { return method; }

    public String getPath() { return path; } // 이제 연산 없이 바로 리턴

    public String getQueryString() { return queryString; }

}