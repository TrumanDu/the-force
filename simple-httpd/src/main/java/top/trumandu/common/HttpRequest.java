package top.trumandu.common;

import java.util.List;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public class HttpRequest {
    private String path;
    private String version;
    private HttpMethod method;
    private Headers headers;

    public HttpRequest(String method, String path, String version) {
        this.method = HttpMethod.getEnum(method);
        this.path = path;
        this.version = version;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> rawHeaders) {
        this.headers = new Headers(rawHeaders);
    }

    public String getPath() {
        return path;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + version +"\n\r"+headers;
    }
}
