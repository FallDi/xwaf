package ru.nsu.xwaf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author daredevil
 */
public class HTTPRequest {

    private String request;

    /**
     * enumeration of entry HTTP request
     */
    public enum TYPE_ITEM {

        REQUEST_URI_PATH, // url path in first line. maybe contains domain name
        PARAM_VALUE, // all params of cookie, get, post
        REQUEST_LINE, // first string in request, such as GET / HTTP/1.0
        PARAM_NAME, // all params of cookie, get, post
    }

    /**
     *
     * @param http request
     */
    public HTTPRequest(String request) {
        this.request = request;
    }

    /**
     *
     * @param type
     * @return Set of requested type entries
     */
    public Set<String> getItemType(TYPE_ITEM type) {
        Set<String> result = new HashSet<String>();

        switch (type) {
            case REQUEST_LINE:
                result.add(request.split("\r\n")[0]);
                break;
            case REQUEST_URI_PATH:
                String splits[] = request.split("[\\s+]");
                result.add(splits[1]);
                break;
            case PARAM_VALUE:
                Map<String, String> postParams = getPostParams();
                Map<String, String> cookieParams = getCookieParams();
                Map<String, String> getParams = getGetParams();
                if (null != postParams) {
                    for (Entry<String, String> value : postParams.entrySet()) {
                        result.add(value.getValue());
                    }
                }
                if (null != cookieParams) {
                    for (Entry<String, String> value : cookieParams.entrySet()) {
                        result.add(value.getValue());
                    }
                }
                if (null != getParams) {
                    for (Entry<String, String> value : getParams.entrySet()) {
                        result.add(value.getValue());
                    }
                }
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    /**
     *
     * @return http method(GET, POST, ...)
     */
    public String getMethod() {
        return request.split(" ")[0];
    }

    /**
     *
     * @return http request fields
     */
    public Map<String, String> getFields() {
        Map<String, String> map = new HashMap<String, String>();
        String lines[] = request.split("\r\n");
        for (int i = 1; i < lines.length; ++i) {
            String[] fields = lines[i].split(":");
            if (2 == fields.length) {
                map.put(fields[0].trim(), fields[1].trim());
            }
        }
        return map;
    }

    /**
     *
     * @return post parameters string, or null
     */
    public String getPostParamsString() {
        String f[] = request.split("\r\n\r\n");
        if (2 == f.length) {
            return f[1];
        }
        return null;
    }

    /**
     *
     * @return map with parameters names and parameters values, or null
     */
    public Map<String, String> getGetParams() {
        Map<String, String> map = new HashMap<String, String>();
        String getURLS[] = request.split("\n")[0].split("[?]", 2);
        if (getURLS.length == 2) {
            String getParams[] = getURLS[1].split(" ")[0].split("[&]");
            for (int i = 0; i < getParams.length; ++i) {
                String name = getParams[i].split("[=]", 2)[0];
                String value = getParams[i].split("[=]", 2)[1];
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     *
     * @return map with parameters names and parameters values, or null
     */
    public Map<String, String> getPostParams() {
        Map<String, String> map = new HashMap<String, String>();
        String postParamsStr = this.getPostParamsString();
        if (null != postParamsStr) {
            String paramValue[] = postParamsStr.split("&");
            String paramStr[];
            for (int i = 0; i < paramValue.length; ++i) {
                paramStr = paramValue[i].split("=");
                if (2 == paramStr.length) {
                    map.put(paramStr[0], paramStr[1]);
                }
            }
            return map;
        }
        return null;
    }

    /**
     *
     * @return string cookie parameters or null
     */
    public String getCookieParamsString() {
        int start = request.indexOf("Cookie: ");
        int end = request.indexOf("\r\n", start);
        if ((-1 != start) && (-1 != end)) {
            start += "Cookie: ".length();
            return request.substring(start, end);
        }
        return null;
    }

    /**
     *
     * @return map with parameters names and parameters values, or null
     */
    public Map<String, String> getCookieParams() {
        Map<String, String> map = new HashMap<String, String>();
        String cookieParamsStr = this.getCookieParamsString();
        if (null != cookieParamsStr) {
            String paramValue[] = cookieParamsStr.split(";");
            String paramStr[];
            for (int i = 0; i < paramValue.length; ++i) {
                paramStr = paramValue[i].split("=");
                if (2 == paramStr.length) {
                    map.put(paramStr[0].trim(), paramStr[1].trim());
                }
            }
            return map;
        }
        return null;
    }
}
