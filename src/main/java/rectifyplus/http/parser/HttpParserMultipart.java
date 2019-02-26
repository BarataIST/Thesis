package rectifyplus.http.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;

public class HttpParserMultipart {
	 
	
	
	public static boolean isMultipart(HttpRequest req) {
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(req);
		return decoder.isMultipart();
	}
	
	public static List<InterfaceHttpData> getHttpDatas(HttpRequest req){
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(req);
		List<InterfaceHttpData> aux = decoder.getBodyHttpDatas();
		for(InterfaceHttpData e : aux) {
			System.out.println("NOVO NOME: " + e.getName());
		}
		return aux;
	}
	
	public static void parse(String json)  {
		JSONObject object = new JSONObject(json);
		String[] keys = JSONObject.getNames(object);

		for (String key : keys)
		{
		    Object value = object.get(key);
		    System.out.println("KEY: " + key + " VALUE: " + value.toString());
		}
	}
	
	public static Map<String, List<String>> requestParametersHandler(FullHttpRequest req) {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
	    if (req.getMethod().equals(HttpMethod.POST)) {
	        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
	        try {
	            List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
	            for (InterfaceHttpData data : postList) {
	            	if (data.getHttpDataType() == HttpDataType.Attribute) {
	            		List<String> values = new ArrayList<String>();
	            		Attribute value = (Attribute) data;
	            		value.setCharset(CharsetUtil.UTF_8);
	            		values.add(value.getValue());
	            		parameters.put(data.getName(), values);
	            	}
	            }
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    System.out.println("PARAMETROS: " + parameters);
	    return parameters;
	}
}

