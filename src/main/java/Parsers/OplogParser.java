package Parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class OplogParser {

	public static List<List<String>> parseDoc(Document doc) {
		
		System.out.println("DOCUMENT: " + doc.toString() + "\n");
		Document aux1 = doc;
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> info = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		if(doc.containsKey("$set")) {
			System.out.println("ENTREI");
			aux1 =(Document) doc.get("$set");
		}
		int num = StringUtils.countMatches(aux1.toJson(), ":");
		String[] aux = aux1.toJson().substring(1, aux1.toJson().length()-1).split(",");
		
		for(String i : aux) {
			String[] r = i.split(":",num);
			info.add(r[0]);
			values.add(r[1]);
		}
		
		ret.add(info);
		ret.add(values);
		return ret;
		
	}
}
