package top.trumandu.v3;

import top.trumandu.common.ContextType;
import top.trumandu.common.Headers;
import top.trumandu.common.HttpRequest;
import top.trumandu.common.HttpResponse;
import top.trumandu.util.ResourcesFileUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public class SimpleHttpV3 {

    public void start(int port) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        System.out.println("Simple http server started on port " + port);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int readNum = selector.select();
            if (readNum == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                //处理连接就绪事件
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    handleRequest(selectionKey);
                    // 注册一个写事件，用来给客户端返回信息
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                } else if (selectionKey.isWritable()) {
                    handleResponse(selectionKey);
                }
            }
        }
    }

    private void handleRequest(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        byte[] requestBytes = buffer.array();
        int readBytes;
        while ((readBytes = socketChannel.read(buffer)) != 0) {
            if (readBytes == -1) {
                // 如果读到了流的末尾，则表示连接已经断开
                break;
            }
            // 如果读取到数据，则切换到写模式并处理数据
            buffer.flip();
            int size = buffer.array().length;
            byte[] temp = new byte[requestBytes.length + size];
            System.arraycopy(requestBytes, 0, temp, 0, requestBytes.length);
            System.arraycopy(buffer.array(), 0, temp, requestBytes.length, size);
            requestBytes = temp;
            buffer.clear();
        }
        String context = new String(requestBytes);

        String[] lines = context.split("\r\n");
        String firstLine = null;
        List<String> rawHeaders = new ArrayList<>(64);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i == 0) {
                firstLine = line;
            } else {
                rawHeaders.add(line);
            }
        }
        if (firstLine == null) {
            return;
        }

        String[] array = firstLine.split(" ", 3);
        HttpRequest httpRequest = new HttpRequest(array[0], array[1], array[2]);
        httpRequest.setHeaders(rawHeaders);
        selectionKey.attach(httpRequest);
    }

    private void handleResponse(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        HttpRequest request = (HttpRequest) selectionKey.attachment();
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
        channel.write(ByteBuffer.wrap(response.getFirstLine()));
        channel.write(ByteBuffer.wrap(response.getHeaders().toString().getBytes()));
        channel.write(ByteBuffer.wrap("\r\n".getBytes()));
        channel.write(ByteBuffer.wrap(response.getBody()));
        channel.close();
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
        new SimpleHttpV3().start(port);
    }


}
