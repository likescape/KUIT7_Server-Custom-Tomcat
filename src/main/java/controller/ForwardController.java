package controller;

import db.MemoryUserRepository;
import http.util.*;
import model.User;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        response.forward(request.getPath());
    }
}