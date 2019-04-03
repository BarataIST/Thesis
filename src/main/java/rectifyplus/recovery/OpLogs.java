package rectifyplus.recovery;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.bson.BsonTimestamp;
import org.bson.Document;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import rectifyplus.log.MongoDbCon;

public class OpLogs {
	
	//public static long time = 0;

	public static FindIterable<Document> getOpLogs(Instant time){
		
		MongoClient mongoClient = MongoDbCon.getMongo();
		MongoDatabase database = mongoClient.getDatabase("local");
		MongoCollection<Document> oplogs = database.getCollection("oplog.$main");
		BsonTimestamp newTime = new BsonTimestamp((int)time.plus(2,ChronoUnit.SECONDS).getEpochSecond(),0);
		BsonTimestamp lastReadTimestamp = new BsonTimestamp((int)time.getEpochSecond(),0);
		
		Document filter = new Document("$gt", lastReadTimestamp);
		filter.append("$lt", newTime);
		FindIterable<Document> it = oplogs.find(new Document("ts",filter));
		System.out.println("CENAAA: " + it.first().toString() + "\n");
		return it;
	}
}
