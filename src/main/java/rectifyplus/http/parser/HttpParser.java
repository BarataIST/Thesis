package rectifyplus.http.parser;    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;


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
    
    public static List<List<String>> parseFullHttpRequest(FullHttpRequest request){
    	List<List<String>> info = new ArrayList<List<String>>();
    	List<String> args = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
    	if((request.getMethod().toString().equals("POST") || request.getMethod().toString().equals("PUT")) && 
      		   request.headers().get("Content-Type").contains("application/json")) {
         	CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
         	String contentStr = contentBuf.toString(CharsetUtil.UTF_8);
         	JSONObject object = new JSONObject(contentStr);
     		String[] keys = JSONObject.getNames(object);
     		for (String key : keys)
     		{
     		    Object value = object.get(key);
     		    args.add(key);
     		    values.add(value.toString());
     		}
     		info.add(args);
            info.add(values);
     		System.out.println("NAME: " + args + "\n");
             System.out.println("VALUES: " + values + "\n");
         }else if(request.getMethod().toString().equals("POST") && 
      		   request.headers().get("Content-Type").contains("multipart/form-data")){
         	Map<String, List<String>> data = HttpParserMultipart.requestParametersHandler(request);
         	for(Map.Entry<String, List<String>> entry : data.entrySet()) {
         		args.add(entry.getKey());
         		values.addAll(entry.getValue());
         	}
         	info.add(args);
            info.add(values);
         	//numOfArgs = args.size();
         	System.out.println("ARGUMENTOS: " + args + "\n");
         	System.out.println("Valores: " + values + "\n");
         	//System.out.println("#Args: " + numOfArgs + "\n");
         }else if((request.getMethod().toString().equals("POST") || request.getMethod().toString().equals("PUT")) && 
       		   request.headers().get("Content-Type").contains("application/x-www-form-urlencoded")) {
         	CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
         	String contentStr = contentBuf.toString(CharsetUtil.UTF_8);
         	String[] pairs = contentStr.split("\\&");
             for (int i = 0; i < pairs.length; i++) {
               String[] fields = pairs[i].split("=");
               args.add(fields[0]);             
               values.add(fields[1]);
             }
             info.add(args);
             info.add(values);
             System.out.println("NAME: " + args + "\n");
             System.out.println("VALUES: " + values + "\n");
         }
    	return info;
    }

}
