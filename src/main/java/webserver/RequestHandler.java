package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());


        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            if (line == null) return;


            String[] tokens = line.split(" ");
            String method = tokens[0]; // GET 또는 POST 추출
            String url = tokens[1];
            boolean logined = false;
            int contentLength = 0;
            String contentType = "text/html";

            if (url.endsWith(".css")) {
                contentType = "text/css";
            }
//            위와 같은 꼴로 들어오고 이때 contentLength를 끌어와야함
//             POST /register HTTP/1.1
//            Host: example.com
//            Content-Type: application/json
//            Content-Length: 45
//
//            {
//              "username": "gemini_user",
//              "email": "hello@example.com"
//            }

            while(true) {
                line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie")) {
                    logined = line.contains("logined=true");
                }
            }
            if(url.equals("/")){
                url = "/index.html";
            }


            if ("POST".equals(method) && url.startsWith("/user/signup")) {
                // Body 읽기
                String bodyData = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(bodyData);

                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                log.info("User Created (POST) : " + user);
                MemoryUserRepository.getInstance().addUser(user);

                response302Header(dos, "/index.html");
            }
            else if ("POST".equals(method) && url.startsWith("/user/login")) {
                String bodyData = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(bodyData);

                // 저장된 유저 찾기
                User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

                // 로그인 성공 여부 확인
                if (user != null && user.getPassword().equals(params.get("password"))) {
                    log.info("Login Success: " + user.getUserId());
                    // 쿠키를 포함한 302 응답
                    response302HeaderWithCookie(dos, "/index.html", "logined=true");
                } else {
                    log.info("Login Failed");
                    // 실패 시 로그인 실패 페이지로 이동
                    response302Header(dos, "/user/login_failed.html");
                }
            }
            else if (url.startsWith("/user/userList")) {
                if(!logined){
                    log.info("user isn't logged in");
                    response302Header(dos, "/user/login.html")  ;
                    return;
                }

                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                response200Header(dos, body.length,contentType);
                responseBody(dos, body);

            }
            else{
                byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));
                response200Header(dos, body.length,contentType);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path, String cookie) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + path + "\r\n");
        dos.writeBytes("Set-Cookie: " + cookie + "; Path=/\r\n"); // 쿠키 설정
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void response302Header(DataOutputStream dos, String path) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}