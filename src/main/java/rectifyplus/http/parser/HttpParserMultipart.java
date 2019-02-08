package rectifyplus.http.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		//InterfaceHttpData data = decoder.getBodyHttpData("content.extended");
		/*if (data.getHttpDataType() == HttpDataType.Attribute) {
			Attribute attribute = (Attribute) data;
			String value = null;
			try {
				value = attribute.getValue();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("VALOR DO TITLO: " + value + "\n"); 
		}*/
		List<InterfaceHttpData> aux = decoder.getBodyHttpDatas();
		for(InterfaceHttpData e : aux) {
			System.out.println("NOVO NOME: " + e.getName());
		}
		return aux;
	}
	
	public static Map<String, List<String>> requestParametersHandler(HttpRequest req) {
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
	    return parameters;
	}
	
	
}

