package top.trumandu.common;

/**
 * @author Truman.P.Du
 * @date 2023/03/25
 * @description
 */
public class ContextType {
    public static String getContextType(String suffix) {
        assert suffix != null;
        switch (suffix.toLowerCase()) {
            case "html":
                return "text/html; charset=utf-8";
            case "css":
                return "text/css; charset=utf-8";
            case "js":
                return "text/javascript; charset=utf-8";
            case "jpeg":
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "ico":
                return "image/x-icon";
            case "json":
                return "application/json; charset=utf-8";
            default:
                return "text/plain; charset=utf-8";
        }

    }
}
