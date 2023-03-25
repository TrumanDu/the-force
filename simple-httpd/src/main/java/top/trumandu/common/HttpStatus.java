package top.trumandu.common;

/**
 * @author Truman.P.Du
 * @date 2023/03/25
 * @description
 */
public enum HttpStatus {
    /**
     *
     */
    OK(200, "OK"), NOT_FOUND(404, "Not Found."), INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    /**
     *
     */
    private int code;
    private String context;

    public int getCode() {
        return code;
    }

    public String getContext() {
        return context;
    }

    HttpStatus(int code, String context) {
        this.code = code;
        this.context = context;
    }

    public static HttpStatus getEnum(int code) {
        HttpStatus[] values = HttpStatus.values();
        for (HttpStatus value : values) {
            if (value.code == code) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return code + " " + context;
    }
}
