package controller;

import db.MemoryUserRepository;
import http.util.*;
import model.User;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        MemoryUserRepository.getInstance().addUser(user);
        response.redirect("/index.html");
    }
}