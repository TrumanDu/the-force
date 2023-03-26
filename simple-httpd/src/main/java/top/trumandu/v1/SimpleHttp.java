package top.trumandu.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public class SimpleHttp {
    private static int port;
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Simple http server started on port " + port);

        while (true) {
            Socket socket = ss.accept();
            System.out.println("New client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                System.out.println(line);
            }
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
}
