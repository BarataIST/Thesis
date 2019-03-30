package rectifyplus.recovery;



import java.sql.Timestamp;
import java.time.Instant;

import io.netty.handler.codec.http.FullHttpRequest;

public class CreateSignRec {
	
	public static void CreateSigRec(FullHttpRequest request, Instant timestamp) {
		OpLogs.getOpLogs(timestamp);
		
	}

}
