package rectifyplus.recovery;

import java.sql.Timestamp;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import rectifyplus.log.MongoDbCon;

public class OpLogs {
	
	public static long time = 0;

	public static void getOpLogs(){
		
		MongoClient mongoClient = MongoDbCon.getMongo();
		MongoDatabase database = mongoClient.getDatabase("local");
		MongoCollection<Document> oplogs = database.getCollection("oplog.$main");
		Timestamp newTime = new Timestamp(time + 5 * 1000);
		BsonTimestamp lastReadTimestamp = new BsonTimestamp(time);
		
		Document filter = new Document("$gt", lastReadTimestamp);
		filter.append("$lt", newTime);
		Bson filter2 = filter;
		FindIterable<Document> it = oplogs.find(new Document("ts",filter2));
		System.out.println("OPLOGS" + it.first().toString());
	}
	
	public static void setTime(long timeStamp) {
		time = timeStamp;
	}
}
