package http.util;

public enum HttpMethod {
    GET, POST, UNKNOWN; // 처리할 수 없는 메서드용 상수를 하나 둡니다.

    public static HttpMethod of(String name) {
        try {
            return HttpMethod.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN; // 예외 대신 UNKNOWN 반환
        }
    }
}