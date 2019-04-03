package Parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class OplogParser {

	public static List<List<String>> parseDoc(Document doc) {
		
		List<List<String>> ret = new ArrayList<List<String>>();
		List<String> info = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		int num = StringUtils.countMatches(doc.toJson(), ":");
		String[] aux = doc.toJson().substring(1, doc.toJson().length()-1).split(",");
		
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
