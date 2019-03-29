package rectifyplus.recovery;



import java.sql.Timestamp;

import io.netty.handler.codec.http.FullHttpRequest;

public class CreateSignRec {
	
	public static void CreateSigRec(FullHttpRequest request, long timestamp) {
		OpLogs.getOpLogs(timestamp);
		
	}

}
