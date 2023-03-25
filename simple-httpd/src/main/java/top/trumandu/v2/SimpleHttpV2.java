package top.trumandu.v2;

import top.trumandu.common.ContextType;
import top.trumandu.common.Headers;
import top.trumandu.common.HttpRequest;
import top.trumandu.common.HttpResponse;
import top.trumandu.util.ResourcesFileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public class SimpleHttpV2 {
    private static int port;

    public void start(int port) throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Simple http server started on port " + port);
        HttpResponse response;
        while (true) {
            Socket socket = ss.accept();
            try {
                HttpRequest request = handleRequest(socket);
                if (request == null) {
                    continue;
                }
                log(request);
                response = handleResponse(request, socket);
            } catch (Exception e) {
                response = handle5xx();
            }
            response(response, socket);
        }
    }

    private HttpRequest handleRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        List<String> rawHeaders = new ArrayList<>(64);
        String firstLine = null;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            if (firstLine == null) {
                firstLine = line;
            } else {
                rawHeaders.add(line);
            }
        }

        if (firstLine == null) {
            return null;
        }

        String[] array = firstLine.split(" ", 3);
        HttpRequest httpRequest = new HttpRequest(array[0], array[1], array[2]);
        httpRequest.setHeaders(rawHeaders);
        return httpRequest;
    }

    private HttpResponse handleResponse(HttpRequest request, Socket socket) throws IOException {
        String path = request.getPath();
        HttpResponse response;
        try {
            if ("/".equalsIgnoreCase(path) || path.length() == 0) {
                path = "/index.html";
            } else if (path.indexOf(".") < 0) {
                path = path + ".html";
            }
            boolean flag = ResourcesFileUtil.isExistFile(path);
            if (!flag) {
                path = request.getPath() + "/index.html";
                flag = ResourcesFileUtil.isExistFile(path);
            }
            if (!flag) {
                response = handle404();
            } else {
                response = handleOk(path);
            }
        } catch (Exception e) {
            response = handle5xx();
        }
        return response;
    }

    private void response(HttpResponse response, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getFirstLine());
        outputStream.write(response.getHeaders().toString().getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write(response.getBody());
        outputStream.flush();
        outputStream.close();
    }

    private HttpResponse handleOk(String path) throws IOException {

        String suffix = "html";
        if (path.lastIndexOf(".") > 0) {
            suffix = path.substring(path.lastIndexOf(".") + 1, path.length());
        }
        byte[] body = ResourcesFileUtil.getResource(path);
        HttpResponse response = new HttpResponse(200, body);
        Headers headers = new Headers();
        headers.addHeader("Content-Type", ContextType.getContextType(suffix));
        headers.addHeader("Content-Length", "" + body.length);
        response.setHeaders(headers);
        return response;
    }

    private HttpResponse handle404() throws IOException {
        String body = "Page not fond!";
        HttpResponse response = new HttpResponse(404, body.getBytes());
        Headers headers = new Headers();
        headers.addHeader("Content-Type", ContextType.getContextType("html"));
        headers.addHeader("Content-Length", body.getBytes().length + "");
        headers.addHeader("Connection", "Close");
        response.setHeaders(headers);
        return response;
    }

    private HttpResponse handle5xx() throws IOException {
        String body = "Internal Server Error!";
        HttpResponse response = new HttpResponse(500, body.getBytes());
        Headers headers = new Headers();
        headers.addHeader("Content-Type", ContextType.getContextType("html"));
        headers.addHeader("Content-Length", body.getBytes().length + "");
        headers.addHeader("Connection", "Close");
        response.setHeaders(headers);
        return response;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void log(HttpRequest request) {
        String log = String.format("%s INFO %s %s %s", formatter.format(LocalDateTime.now()),
                request.getMethod(), request.getPath(), request.getHeaders().getHeader("User-Agent"));
        System.out.println(log);
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        new SimpleHttpV2().start(port);
    }


}
