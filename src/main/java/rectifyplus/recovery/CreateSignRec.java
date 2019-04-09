package rectifyplus.recovery;



import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;


import io.netty.handler.codec.http.FullHttpRequest;
import rectifyplus.Parsers.OplogParser;
import rectifyplus.log.MongoDbCon;

public class CreateSignRec {
	
	public static void CreateSigRec(FullHttpRequest request, List<List<String>> content, Instant timestamp) {
		List<List<String>> info = new ArrayList<List<String>>();
		List<Document> oplogParsed = new ArrayList<Document>();
		
		FindIterable<Document> oplogs = OpLogs.getOpLogs(timestamp);
		
		
		if(!oplogs.equals(null)) {
			for(Document i : oplogs) {
				Document aux1 = (Document) i.get("o");
				Document aux = new Document("type",i.get("op"));
				if(!aux1.isEmpty()) {
					info = OplogParser.parseDoc(aux1);
					aux.append("columns", info.get(0)).
					append("number of columns", Integer.toString(info.get(0).size())).
					append("values", info.get(1));
				}
				aux.append("namespace", i.get("ns"));
				oplogParsed.add(aux);
			}
		}
		
		Document httpDoc = new Document("Method",request.getMethod().toString()).
				append("URI", request.getUri().toString()).
				append("Number of Param", Integer.toString(content.get(0).size())).
				append("Parameters", content.get(0).toString()).
				append("Values", content.get(1).toString());
		
		Document signRec = new Document("HttpRequest",httpDoc);
		if(!oplogs.equals(null)) {
			signRec.append("Nr of Statements", Integer.toString(oplogParsed.size()));
			int j = 0;
			for(Document i : oplogParsed) {
				j = j + 1;
				String rec = "NoSQL" + Integer.toString(j);
				signRec.append(rec, i);
			}
		}
		MongoDatabase db = MongoDbCon.getMongoToTeach();
		db.getCollection("SignRecords").insertOne(signRec);
	}

}
