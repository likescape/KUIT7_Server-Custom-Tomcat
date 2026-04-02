package webserver;

import controller.*;
import http.util.HttpRequest;
import http.util.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private static final Map<String, Controller> controllers = new HashMap<>();

    // 클래스 로딩 시점에 매핑 정보를 초기화합니다.
    static {
        controllers.put("/", new ForwardController()); // 홈 화면
        controllers.put("/index.html", new ForwardController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
    }

    private final HttpRequest request;
    private final HttpResponse response;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void proceed() {
        // 1. 매핑된 컨트롤러가 있는지 확인
        Controller controller = controllers.get(request.getPath());

        // 2. 없으면 기본적으로 정적 파일을 찾는 ForwardController 사용
        if (controller == null) {
            controller = new ForwardController();
        }

        // 3. 실행
        controller.execute(request, response);
    }
}