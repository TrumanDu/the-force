package top.trumandu.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Truman.P.Du
 * @date 2023/03/25
 * @description
 */
public class HttpResponse {
    private String version;
    private HttpStatus status;
    private Headers headers;
    private byte[] body;

    public HttpResponse(int code) {
        this.init("HTTP/1.1", code, null, null);

    }

    public HttpResponse(int code, List<String> rawHeaders) {
        this.init("HTTP/1.1", code, rawHeaders, null);
    }

    public HttpResponse(int code, byte[] body) {
        this.init("HTTP/1.1", code, new ArrayList<>(), body);
    }

    public HttpResponse(int code, List<String> rawHeaders, byte[] body) {
        this.init("HTTP/1.1", code, rawHeaders, body);
    }

    public HttpResponse(String version, int code, List<String> rawHeaders) {
        this.init(version, code, rawHeaders, body);
    }

    private void init(String version, int code, List<String> rawHeaders, byte[] body) {
        this.version = version;
        this.status = HttpStatus.getEnum(code);
        rawHeaders.add("Connection:Close");
        this.headers = new Headers(rawHeaders);
        this.body = body;
    }


    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public byte[] getFirstLine() {
        byte[] line = (version + " " + status.toString() + "\n\r").getBytes();
        return line;
    }

    /**
     * 涉及数组拷贝，不推荐使用
     *
     * @return
     */
    public byte[] getResponse() {
        byte[] line = (version + " " + status.toString() + "\n\r" + headers + "\n\r").getBytes();
        byte[] result = new byte[line.length + body.length];
        System.arraycopy(line, 0, result, 0, line.length);
        System.arraycopy(body, 0, result, line.length, body.length);
        return result;
    }


    @Override
    public String toString() {
        return version + " " + status.toString() + "\n\r" + headers + "\n\r";
    }
}
