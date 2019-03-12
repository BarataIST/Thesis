package rectifyplus.recovery;

import com.mongodb.client.MongoClient;

public class MongoConnectionHandler {
	
	private static String connectionString;
	private String databaseName;
	private String collection;
	public static MongoClient client;
	
	public MongoConnectionHandler(String connectionString, String databaseName, String collection) {
		MongoConnectionHandler.connectionString = connectionString;
		this.databaseName = databaseName;
		this.collection = collection;
		//client = new MongoClient("localhost");
		
	}
	
	
	
	

}
