package rectifyplus.http.parser;    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */


import java.util.ArrayList;


/**
 *
 * @author davidmatos
 */
public class HttpParser {

    public static String getMethod(String httpRequest) {
        String httpRequestPart = httpRequest.substring(httpRequest.indexOf(")") + 2);
        httpRequestPart = httpRequestPart.substring(0, 7);

        return httpRequestPart.substring(0, httpRequestPart.indexOf(" ")).trim();
    }

    public static String getHost(String httpRequest) {
        String httpRequestPart = httpRequest.substring(httpRequest.indexOf("Host: ") + 6);

        return httpRequestPart;//.substring(0, httpRequestPart.indexOf("Accept")).trim();
    }

    public static String getUri(String httpRequest) {
        String httpRequestPart = httpRequest.substring(httpRequest.indexOf(")") + 2);
        httpRequestPart = httpRequestPart.substring(httpRequestPart.indexOf(" "), httpRequestPart.indexOf("HTTP/1"));
        return httpRequestPart.trim();
    }

    public static ArrayList<String> getGetParameters(String request) {
        ArrayList<String> result = new ArrayList<>();
        String uri = getUri(request);
        if (uri.indexOf("?") > 0) {

            String query = uri.substring(uri.indexOf("?"));
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                result.add(pair.split("=")[0]);
            }
        }

        return result;
    }

    public static ArrayList<String> getGetValues(String request) {
        ArrayList<String> result = new ArrayList<>();
        String uri = getUri(request);
        if (uri.indexOf("?") > 0) {

            String query = uri.substring(uri.indexOf("?")+1);
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                result.add(pair.split("=")[1]);
            }
        }
        return result;

    }

}
