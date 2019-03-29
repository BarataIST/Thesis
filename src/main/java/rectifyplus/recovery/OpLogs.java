package rectifyplus.recovery;






import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
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

	public static void getOpLogs(long time){
		
		
		MongoClient mongoClient = MongoDbCon.getMongo();
		MongoDatabase database = mongoClient.getDatabase("local");
		MongoCollection<Document> oplogs = database.getCollection("oplog.$main");
		System.out.println("\nYOOOOOOOOOOOOOOOOOOOOOOOO\n");
		System.out.println("OLHA A DATA: " + new BsonTimestamp(Instant.now().getEpochSecond()) + "\n");
		System.out.println("OLHA O OP LOG: " + oplogs.find().first().toString() + "\n");
		Date date =  new Date();
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		BsonTimestamp test = new BsonTimestamp((int)Calendar.getInstance().getTimeInMillis()/1000,1);
		BSONTimestamp test2 = new BSONTimestamp((int)Calendar.getInstance().getTimeInMillis()/1000,1);
		
		
		String data = Calendar.getInstance().getTime().toString();
		//Timestamp tp = Timestamp.valueOf(Calendar.getInstance().getTime().toString());
		
		
		System.out.println("TESTTTT: " + timestamp + "\n");
		//System.out.println("TEST DE TIMESTAMP " + tp.toString() + "\n");
		BsonTimestamp newTime = new BsonTimestamp(time + 5 * 1000);
		BsonTimestamp lastReadTimestamp = new BsonTimestamp(time/1000);
		
		Document filter = new Document("$gt", lastReadTimestamp);
		Document filter123 = new Document("ts",filter);
		
		Bson filter3 = filter123;
		System.out.println("DOCUMENT CREATED: " + filter3 + "\n");
		FindIterable<Document> it = oplogs.find(new Document("ts",filter3));
		System.out.println("OLHA O FILTRO: " + filter.toString() + "\n");
		filter.append("$lt", newTime);
		//System.out.println("OLHA O FILTRO: " + filter.toString() + "\n");
		Bson filter2 = filter;
		
		
		
		it.cursorType(CursorType.Tailable);
		
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
