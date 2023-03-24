package top.trumandu.common;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public enum HttpMethod {
    /**
     *
     */
    GET, POST, PUT, OPTION;

    public static HttpMethod getEnum(String method) {
        HttpMethod[] values = HttpMethod.values();
        for (HttpMethod value : values) {
            String name = value.name();
            if (name.equalsIgnoreCase(method)) {
                return value;
            }
        }
        return null;
    }
}
