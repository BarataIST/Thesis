package rectifyplus.wekaConnection;

import java.io.File;
import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.sql.*;
import java.util.StringTokenizer;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

public class WekaCon {
	
	public static void connectTest() {
		
		/*try {
			  Class.forName("mongodb.jdbc.MongoDriver");
			} catch (ClassNotFoundException e) {
			  System.out.println("ERROR: Unable to load mongodb JDBC Driver");
			  e.printStackTrace();
			  return;
			}*/
		/*InstanceQuery query = new InstanceQuery();
		//query.setDatabaseURL("jdbc:mongo://localhost:27017/trainingDB");
		query.setQuery("select * from SignRecords");
		Instances data = query.retrieveInstances();
		System.out.println("A DATA NA CENA" + data.toString() + "\n");*/
	}
	
	public static void classifyTrainingSet() throws IOException{
		Process p = null;
		String command = "mongoexport --db trainingDB --collection SignRecords --out /home/rui/Desktop/signRecords.json";
		
		StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        System.out.println("CMDARRAY:" + cmdarray + "\n");
		ProcessBuilder pb = new ProcessBuilder(cmdarray);
		//pb.directory(new File("/usr/bin"));
		pb.start();
		/*MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase database = mongoClient.getDatabase("trainingDB");*/
	}

}
