package rectifyplus.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import rectifyplus.Mode;
import rectifyplus.RectifyProps;
import rectifyplus.http.parser.HttpParserMultipart;



public class MongoDbCon {
	
	private static final String DATABASE_TRAINING_SETS = "trainingDB";
	private static final String DATABASE_LOG = "logDB";
	private static final String DATABASE_TEST = "testDB";
	private static MongoDatabase database = null;
	
	private static void connect() {

		String dbName = RectifyProps.mode == Mode.TEST ? DATABASE_TEST : (RectifyProps.mode == Mode.TRAINNING ? DATABASE_TRAINING_SETS : DATABASE_LOG) ;
		
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		database = mongoClient.getDatabase(dbName);
	}
	
	public static MongoDatabase getDatabase() {
		if (database == null) {
			connect();
		}
		return database;
	}
	
	public static void storeHttpRequest(HttpRequest request, String from) {
		System.out.println(request.toString());
		Document doc = new Document("method",request.getMethod().name())
				.append("uri", request.getUri())
				.append("ts", Calendar.getInstance().getTimeInMillis())
				.append("from", from);
		getDatabase().getCollection("httpRequest").insertOne(doc);
	}
	
	public static void storeFullHttpRequest(FullHttpRequest request, String from) {
		int numOfArgs = 0;
		List<String> args = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
        if(request.getMethod().toString().equals("POST") && 
     		   request.headers().get("Content-Type").contains("application/json")) {
        	CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
            String contentStr = contentBuf.toString(CharsetUtil.UTF_8);
        	contentStr = contentStr.replace("{","");
            contentStr = contentStr.replace("}", "");
            List<String> content = Arrays.asList(contentStr.split(","));
            numOfArgs = content.size();
            for(String s : content) {
            	s = s.replaceAll("\"", "");
            	List<String> aux = Arrays.asList(s.split(":"));
            	String aux2 = aux.get(0);
            	String aux3 = aux.get(1);
            	args.add(aux2);
            	values.add(aux3);
            }
            System.out.println("ARGUMENTOS: " + args + "\n");
        	System.out.println("Valores: " + values + "\n");
        	System.out.println("#Args: " + numOfArgs + "\n");
        }else if(request.getMethod().toString().equals("POST") && 
     		   request.headers().get("Content-Type").contains("multipart/form-data")){
        	Map<String, List<String>> data = HttpParserMultipart.requestParametersHandler(request);
        	for(Map.Entry<String, List<String>> entry : data.entrySet()) {
        		args.add(entry.getKey());
        		values.addAll(entry.getValue());
        	}
        	numOfArgs = args.size();
        	System.out.println("ARGUMENTOS: " + args + "\n");
        	System.out.println("Valores: " + values + "\n");
        	System.out.println("#Args: " + numOfArgs + "\n");
        }
		Document doc = new Document("method",request.getMethod().name())
				.append("uri", request.getUri()).append("numberArgs", numOfArgs)
				.append("args", args).append("values", values)
				.append("ts", Calendar.getInstance().getTimeInMillis());
		getDatabase().getCollection("httpRequest").insertOne(doc);
	}
}
