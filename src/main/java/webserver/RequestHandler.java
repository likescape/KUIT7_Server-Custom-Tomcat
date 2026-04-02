package webserver;

import db.MemoryUserRepository;
import http.util.*;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

// webserver.RequestHandler.java

    @Override
    public void run() {
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // [수정] HttpRequest 생성 전, 첫 줄이 있는지 미리 체크하거나 예외를 잡아서 가볍게 처리
            HttpRequest httpRequest;
            try {
                httpRequest = HttpRequest.from(br);
            } catch (IOException e) {
                // "Empty Request"인 경우 조용히 연결을 닫고 종료
                if (e.getMessage().equals("Empty Request")) {
                    return;
                }
                throw e; // 진짜 에러는 밖으로 던짐
            }

            HttpResponse httpResponse = new HttpResponse(out);

            // 매퍼에게 처리를 위임
            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            // [수정] 무의미한 에러 로그는 거르고 진짜 문제만 로깅
            if (!(e instanceof IOException && e.getMessage().equals("Empty Request"))) {
                log.log(Level.SEVERE, "RequestHandler Error: " + e.getMessage());
            }
        }
    }}