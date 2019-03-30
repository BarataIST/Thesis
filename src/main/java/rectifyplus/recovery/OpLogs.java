package rectifyplus.recovery;






import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BSONTimestamp;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import rectifyplus.log.MongoDbCon;

public class OpLogs {
	
	//public static long time = 0;

	public static void getOpLogs(Instant time){
		
		
		MongoClient mongoClient = MongoDbCon.getMongo();
		MongoDatabase database = mongoClient.getDatabase("local");
		MongoCollection<Document> oplogs = database.getCollection("oplog.$main");
		System.out.println("\nYOOOOOOOOOOOOOOOOOOOOOOOO\n");
		System.out.println("OLHA A DATA: " + new BsonTimestamp((int)Instant.now().getEpochSecond(),1) + "\n");
		System.out.println("OLHA O OP LOG: " + oplogs.find().first().toString() + "\n");
		BsonTimestamp test = new BsonTimestamp((int)Instant.now().getEpochSecond(),0);
		BSONTimestamp test2 = new BSONTimestamp((int)Calendar.getInstance().getTimeInMillis()/1000,0);		
		
		System.out.println("TESTTTT: " + test + "\n");
		BsonTimestamp newTime = new BsonTimestamp((int)time.plus(5,ChronoUnit.SECONDS).getEpochSecond(),0);
		BsonTimestamp lastReadTimestamp = new BsonTimestamp((int)time.getEpochSecond(),0);
		
		Document filter = new Document("$gt", lastReadTimestamp);
		filter.append("$lt", newTime);
		//System.out.println("DOCUMENT CREATED: " + filter3 + "\n");
		FindIterable<Document> it = oplogs.find(new Document("ts",filter));
		System.out.println("OLHA O FILTRO: " + filter.toString() + "\n");
		
		//System.out.println("OLHA O FILTRO: " + filter.toString() + "\n");
		Bson filter2 = filter;
		for(Document i : it) {
			
			System.out.println("O OPLOG " + i.toString() + "\n");
			break;
		}
		//System.out.println("OPLOGS" + it.first().toString());
	}
	
	/*public static void setTime(long timeStamp) {
		time = timeStamp;
	}*/
}
