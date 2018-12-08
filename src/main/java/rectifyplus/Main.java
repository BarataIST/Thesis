package rectifyplus;

import rectifyplus.http.proxy.HTTPProxy;

public class Main {

    public static void main (String [] args){
        HTTPProxy server = new HTTPProxy("http://localhost:3000", 8080);
        server.startProxy();

    }

}
