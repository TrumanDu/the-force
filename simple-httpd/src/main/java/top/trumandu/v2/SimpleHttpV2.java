package top.trumandu.v2;

import top.trumandu.common.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

        while (true) {
            Socket socket = ss.accept();
            HttpRequest request = handleRequest(socket);
            System.out.println(request);

            String response = """
                    HTTP/1.1 200 OK

                    Hello, World!
                    """;

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(response.getBytes());
            outputStream.flush();
            outputStream.close();

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

        String[] array = firstLine.split(" ", 3);
        HttpRequest httpRequest = new HttpRequest(array[0], array[1], array[2]);
        httpRequest.setHeaders(rawHeaders);
        return httpRequest;
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        new SimpleHttpV2().start(port);
    }


}
