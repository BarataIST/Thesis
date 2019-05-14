package rectifyplus;

import java.io.IOException;

import rectifyplus.http.proxy.HTTPProxy;
import rectifyplus.wekaConnection.WekaCon;

public class Main {

    public static void main (String [] args) throws IOException{
    	boolean learning = true;
    	if(learning) {
    		WekaCon.classifyTrainingSet();
    	}else {
    		HTTPProxy server = new HTTPProxy("http://localhost:3000", 8080);
    		server.startProxy();
    	}

    }

}
