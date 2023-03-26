package top.trumandu.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Truman.P.Du
 * @date 2023/03/24
 * @description
 */
public class Headers {

    private Map<String, String> data = new LinkedHashMap<>(64);

    public Headers() {
    }

    public Headers(List<String> rawHeaders) {
        assert rawHeaders == null;
        rawHeaders.forEach(str -> {
            if (str != null && str.indexOf(":") > 0) {
                String name = str.split(":", 2)[0];
                String value = str.split(":", 2)[1];
                data.put(name, value);
            }
        });
    }

    public Headers(Map<String, String> headsMap) {
        data.putAll(headsMap);
    }

    public Map<String, String> getHeads() {
        return new HashMap<>(data);
    }

    public Object getHeader(String headName) {
        return data.get(headName);
    }

    public void addHeader(String headName, String headValue) {
        data.put(headName, headValue);
    }

    public boolean removeHeader(String headName) {
        data.remove(headName);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (data.size() == 0) {
            builder.append("\n\r");
        } else {
            data.forEach((key, value) -> builder.append(key).append(":").append(value).append("\r\n"));
        }

        return builder.toString();
    }
}
