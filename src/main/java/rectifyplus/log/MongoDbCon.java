package rectifyplus.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;
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
    		System.out.println("NAME: " + args + "\n");
            System.out.println("VALUES: " + values + "\n");
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
            System.out.println("NAME: " + args + "\n");
            System.out.println("VALUES: " + values + "\n");
        }
		Document doc = new Document("method",request.getMethod().name())
				.append("uri", request.getUri()).append("numberArgs", numOfArgs)
				.append("args", args).append("values", values)
				.append("ts", Calendar.getInstance().getTimeInMillis());
		getDatabase().getCollection("httpRequest").insertOne(doc);
	}
}
