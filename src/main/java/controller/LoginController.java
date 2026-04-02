package controller;

import db.MemoryUserRepository;
import http.util.*;
import model.User;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        User user = MemoryUserRepository.getInstance().findUserById(request.getParameter("userId"));
        if (user != null && user.getPassword().equals(request.getParameter("password"))) {
            response.addHeader(HttpHeader.SET_COOKIE, "logined=true; Path=/");
            response.redirect("/index.html");
        } else {
            response.redirect("/user/login_failed.html");
        }
    }
}