package rectifyplus.http.proxy;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONObject;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import rectifyplus.http.parser.HttpParserMultipart;
import rectifyplus.log.MongoDbCon;
import rectifyplus.recovery.CreateSignRec;
import rectifyplus.recovery.OpLogs;



public class HTTPProxy {

    private String remoteAddress = "";
    private HttpProxyServer server;
    private int localPort;
    private int countResponse = 0;
    private boolean isTeaching = true;
    private FullHttpRequest requestForTeach = null;
    private long time = 0;

    public HTTPProxy(){}

    public HTTPProxy(String proxiedURL, int localPort) {
        this.remoteAddress = proxiedURL;
        this.localPort = localPort;
    }

    public void startProxy() {
        server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(this.localPort)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                        	@Override
                            public int getMaximumRequestBufferSizeInBytes() {
                                return 512 * 1024;
                            }
                            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                                return new HttpFiltersAdapter(originalRequest) {
                                	
                                	@Override
                                	public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                		// TODO: implement your filtering here                                       
                                		FullHttpRequest request = null;                                                                                                                  
                                		if(httpObject instanceof FullHttpRequest){                                       
                                			request = (FullHttpRequest) httpObject;
                                			CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
                                			String contentStr = contentBuf.toString(CharsetUtil.UTF_8);
                                			if(contentStr.length() != 0) {
                                				if(isTeaching) {
                                					changeValue(1);
                                					System.out.println("CONTENT: " + contentStr  + "\n");
                                					requestForTeach = request;
                                					time = Calendar.getInstance().getTimeInMillis();
                                					
                                					//GUARDAR O PEDIDO HTTP PARA FAZER O SIGNATURE RECORD
                                				}else {
                                					MongoDbCon.storeFullHttpRequest(request, "");
                                				}                              				
                                			}
                                			//System.out.println("CONTENT: " + contentStr + "\n");
                                			request.setUri(remoteAddress + request.getUri().toString());                                             
                                		}
                                		return super.clientToProxyRequest((HttpObject) request);
                                	}

                                    @Override
                                    public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                    	// TODO: implement your filtering here
                                    	if(isTeaching) {
                                    		if(getValue() == 1) {
                                    			System.out.println("ENTREI NA RESPOSTA AO CLIENT");
                                    			try {
													Thread.sleep(3000);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
                                    			CreateSignRec.CreateSigRec(requestForTeach, time);
                                    			changeValue(0);
                                    		} 
                                    	}
                                    	return httpObject;
                                    }
                                };
                            }
                        })
                        .start();
    }
    
    public void changeValue(int value) {
    	this.countResponse = value;
    }
    
    public int getValue() {
    	return this.countResponse;
    }
}
