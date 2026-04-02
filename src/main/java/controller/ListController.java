package controller;

import http.util.*;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        // 1. 로그인 여부 확인 (쿠키의 logined=true 체크)
        if (!request.isLogined()) {
            // 로그인 안 되어 있으면 로그인 페이지로 강제 이동
            response.redirect("/user/login.html");
            return;
        }

        // 2. 로그인 되어 있으면 사용자 목록 페이지(HTML)를 보여줌
        // (요구사항에 따라 현재는 DB 데이터를 직접 넣지 않고 파일만 전달합니다.)
        response.forward("/user/list.html");
    }
}