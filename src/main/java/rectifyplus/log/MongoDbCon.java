package rectifyplus.log;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import rectifyplus.Mode;
import rectifyplus.RectifyProps;



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
		
		/*Iterator<Entry<String, String>>  it = request.headers().iterator();
		Entry<String, String> entry;
		while( (entry = it.next()) != null ){
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}*/
		System.out.println(request.toString());
		Document doc = new Document("method",request.getMethod().name())
				.append("uri", request.getUri())
				.append("ts", Calendar.getInstance().getTimeInMillis())
				.append("from", from);
		getDatabase().getCollection("httpRequest").insertOne(doc);
	}
	
public static void storeFullHttpRequest(FullHttpRequest request) {
		
	}
}
