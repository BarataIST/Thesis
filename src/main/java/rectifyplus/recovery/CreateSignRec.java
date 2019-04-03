package rectifyplus.recovery;



import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import Parsers.OplogParser;
import io.netty.handler.codec.http.FullHttpRequest;
import rectifyplus.log.MongoDbCon;

public class CreateSignRec {
	
	public static void CreateSigRec(FullHttpRequest request, List<List<String>> content, Instant timestamp) {
		List<List<String>> info = new ArrayList<List<String>>();
		List<Document> oplogParsed = new ArrayList<Document>();
		
		FindIterable<Document> oplogs = OpLogs.getOpLogs(timestamp);
		//ESTAVA A DAR PARSE DO OPLOG, VER COMO VEM O CONTENT E CRIAR UM PARSER PARA DIVIDIR NO = E NA , SEGUIDAMENTE CONTAR PARA TER O NUMERO DE 
		//COLUNAS
		MongoDatabase db = MongoDbCon.getMongoToTeach();
		
		
		for(Document i : oplogs) {
			Document aux1 = (Document) i.get("o");
			//System.out.println("OLHA O OPLOG: " + i + "\n");
			info = OplogParser.parseDoc((Document)aux1.get("$set"));
			Document aux = new Document("type",i.get("op")).
					append("number of columns",info.get(0).size()).
					append("columns", info.get(0)).
					append("values", info.get(1)).
					append("namespace", i.get("ns"));
			oplogParsed.add(aux);
		}
		
		Document httpDoc = new Document("Method",request.getMethod()).
				append("URI", request.getUri()).
				append("Number of Param", content.get(0).size()).
				append("Parameters", content.get(0)).
				append("Values", content.get(1));
		
		Document signRec = new Document("HTTP",httpDoc).
				append("Nr of Statements", oplogParsed.size());
		int j = 0;
		for(Document i : oplogParsed) {
			j = j + 1;
			String rec = "NoSQL" + Integer.toString(j);
			signRec.append(rec, i);
		}
		System.out.println("O FINAL" + signRec.toString() + "\n");	
		
		db.getCollection("SignRecords").insertOne(signRec);
	}

}
