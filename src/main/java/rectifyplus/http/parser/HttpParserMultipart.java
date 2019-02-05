package rectifyplus.http.parser;

import java.util.List;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class HttpParserMultipart {
	

	
	
	public static boolean isMultipart(HttpRequest req) {
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(req);
		return decoder.isMultipart();
	}
	
	public static List<InterfaceHttpData> getHttpDatas(HttpRequest req){
		HttpPostMultipartRequestDecoder decoder = new HttpPostMultipartRequestDecoder(req);
		return decoder.getBodyHttpDatas();
	}
	
	
}
