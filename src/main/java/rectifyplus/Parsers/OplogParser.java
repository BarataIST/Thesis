package rectifyplus.Parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class OplogParser {

	public static List<List<String>> parseDoc(Document doc) {
		
		Document aux1 = doc;
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> info = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		if(doc.containsKey("$set")) {
			aux1 =(Document) doc.get("$set");
		}
		
		for(String key : aux1.keySet()) {
			info.add(key);
			values.add(aux1.get(key).toString());			
		}
		System.out.println("INFO: " + info + "\n" + "VALUES: " + values + "\n");
		
		ret.add(info);
		ret.add(values);
		return ret;
		
	}
}
