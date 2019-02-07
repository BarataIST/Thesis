package rectifyplus.http.proxy;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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



public class HTTPProxy {

    private String remoteAddress = "";
    private HttpProxyServer server;
    private int localPort;

    public HTTPProxy(){}

    public HTTPProxy(String proxiedURL, int localPort) {
        this.remoteAddress = proxiedURL;
        this.localPort = localPort;
    }

    public void startProxy() {
      /* server = DefaultHttpProxyServer.bootstrap().withPort(this.localPort).withFiltersSource(new RectifyHTTPFilter())
                .start();
        System.out.println("HTTP Proxy for " + this.remoteAddress + " started on port " + this.localPort);*/
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
                                        //System.out.println(originalRequest.getUri().toString() + "\n");
                                        //System.out.println(remoteAddress + originalRequest.getUri() + "\n");
                                        FullHttpRequest request = null;
                                        
                                        
//                                        System.out.println("isNull?" + originalRequest.get); 
                                       
                                        //System.out.println(originalRequest.toString() + "\n");
                                        //System.out.println("CONTENT TYPE: " + originalRequest.headers().get("Content-Type") + "\n");
                                       // MongoDbCon.storeHttpRequest(originalRequest, "");
                                       if(originalRequest.getMethod().toString().equals("POST") && 
                                    		   originalRequest.headers().get("Content-Type").contains("multipart/form-data")) {
                                        	//System.out.println(originalRequest.headers().get("Content-Type") + "\n");
                                        	System.out.println("ENTREI CRL \n");
                                        	System.out.println("É Multipart: " + HttpParserMultipart.getHttpDatas(originalRequest));
                                        }
                                      
                                        
                                        if(httpObject instanceof FullHttpRequest){
                                        	//System.out.println("ENTREI!");
                                            request = (FullHttpRequest) httpObject;
                                            MongoDbCon.storeFullHttpRequest(request, "");
                                            CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
                                            String contentStr = contentBuf.toString(CharsetUtil.UTF_8);
                                            //System.out.println("CONTEUDO: " + contentStr + "\n");
                                            //JSONObject obj = new JSONObject(contentStr);
                                            //System.out.println("CONTENT: " + obj.getString("password"));
                                            request.setUri(remoteAddress + request.getUri().toString()); 
                                            //System.out.println("URI:" + request.getUri().toString() + "\n");
                                            //JSONObject obj = new JSONObject(contentStr);
                                            //System.out.println(obj.toString());
                                            //System.out.println(request.content().toString());
                                            //request.set
                                        }
                                        
                                        return super.clientToProxyRequest((HttpObject) request);
                                        /*try {    
                                            return getHttpResponse( (HttpURLConnection) new URL(remoteAddress + originalRequest.getUri()).openConnection());
                                        }catch(MalformedURLException e1)  {
                                            System.out.println("Exception 1");
                                            return null;
                                        }catch (IOException e1){
                                            System.out.println("Exception 2");
                                            return null;
                                        }*/

                                    }

                                    @Override
                                    public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                        // TODO: implement your filtering here
                                        //System.out.println("RESPOSTA:" + httpObject.toString());
                                        return httpObject;
                                    }
                                };
                            }
                        })
                        .start();

    }


    class RectifyHTTPFilter extends HttpFiltersSourceAdapter {

        @Override
        public int getMaximumRequestBufferSizeInBytes() {
            return 512 * 1024;
        }

        @Override
        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new HttpFiltersAdapter(originalRequest) {

                @Override
                public HttpResponse clientToProxyRequest(HttpObject httpObject) {

                    if (originalRequest.getUri().contains("favicon")) {
                        return null;
                    }

                    FullHttpRequest request = null;

                    if(httpObject instanceof FullHttpRequest){
                        request = (FullHttpRequest) httpObject;
                        CompositeByteBuf contentBuf = (CompositeByteBuf) request.content();
                        String contentStr = contentBuf.toString(CharsetUtil.UTF_8);

                       // JSONObject obj = new JSONObject(contentStr);
                       // System.out.println(obj.toString());
                        //System.out.println(request.content().toString());
                    }




                   // System.out.println(originalRequest.headers().toString());
                   // System.out.println(originalRequest.toString() + "\n");
                    //ParsedHttpRequest parsedRequest = new ParsedHttpRequest(originalRequest.toString());
                   // originalRequest.setUri(remoteAddress + originalRequest.getUri());

                    System.out.println(originalRequest.getUri() + "\n" + originalRequest.getMethod().toString() + "\n" + originalRequest.getDecoderResult().toString() +
                             "\n" + originalRequest.getProtocolVersion().toString() + "\n" + originalRequest.getClass().toString());



//                    rectifyplus.AsyncLogWriter.getInstance().addLogHttpRequest(originalRequest.toString(), originalRequest.getUri());
                    originalRequest.setUri(remoteAddress + originalRequest.getUri());

                   /* if (Rectify.getInstance().isInTeachingMode()) {
                        // Training mode. Should store every
                        // request in the KB
                        if (Rectify.getInstance().getCurrentKbHttpRequest() == null) {
                            Rectify.getInstance().setCurrentKbHttpRequest(rectifyplus.http.parser.HttpParser.getKbHttpRequest(originalRequest.toString()));
                            if(Rectify.getInstance().getCurrentKbHttpRequest() == null){
                                System.out.println("NULLL");
                            }
                        }
                    } else {
                        // Normal mode. Should store every
                        // request in the DB Log

                        rectifyplus.AsyncLogWriter.getInstance().addLogHttpRequest(originalRequest.toString());

                    }*/

                   URL obj = null;

                    try {
                        System.out.println(originalRequest.getUri());
                        obj = new URL(originalRequest.getUri());
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }

                    HttpURLConnection con = null;
                    try {
                        con = (HttpURLConnection) obj.openConnection();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    // optional default is GET
                    try {
                        con.setRequestMethod(originalRequest.getMethod().name());
                        con.setDoInput(true);
                        con.setDoOutput(true);

                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());

                        wr.writeBytes(request.content().toString());
                        wr.flush();
                        wr.close();


                    } catch (ProtocolException e1) {
                        e1.printStackTrace();
                    }catch (IOException e1){
                        e1.printStackTrace();
                    }

                    // add request header
                    // con.setRequestProperty("User-Agent", originalRequest.g);
                    int responseCode = 0;
                    try {
                        responseCode = con.getResponseCode();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    HttpResponse httpResponse = getHttpResponse(con);



                   /* if (Rectify.getInstance().isInTeachingMode()) {
                        // Training mode. Should store every
                        // request in the KB
//                        if (Rectify.getInstance().getCurrentKbHttpResponse() != null) {
//                            // Rectify.currentKbHttpRequest.setKbDbOps(Rectify.currentKbDbOps);
//                            Set<KbHttpResponse> setKbHttpResponses = new HashSet<KbHttpResponse>();
//                            setKbHttpResponses.add(new KbHttpResponse(Rectify.getInstance().getCurrentKbHttpRequest(), new Date()));
//                            Rectify.getInstance().getCurrentKbHttpRequest().setKbHttpResponses(setKbHttpResponses);
//                        }
                    } else {

//                        rectifyplus.AsyncLogWriter.getInstance().addLogHttpRequest(originalRequest.toString(),
//                                originalRequest.getUri(), , ctx.channel().localAddress().toString());
                    }*/

                    return httpResponse;
                }

                @Override
                public HttpObject serverToProxyResponse(HttpObject httpObject) {
                    return httpObject;
                }
            };
        }
    }

    private HttpResponse getHttpResponse(HttpURLConnection con) {
//        BufferedReader in = null;
//        InputStreamReader isr = null;
        byte[] buf = new byte[1000000000];
        BufferedInputStream bis = null;
        try {
        	
//        	ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        	
        	 bis = new BufferedInputStream(con.getInputStream());
        	
        	
//            isr = new InputStreamReader(con.getInputStream());

//            in = new BufferedReader(isr);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
//        String inputLine;
//        StringBuffer response = new StringBuffer();  
        try {
        	
        	buf = bis.readAllBytes();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            bis.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ByteBuf buffer = null;
//        try {
//        	String respStr = new String(buf);
//            buffer = Unpooled.wrappedBuffer(respStr
//                    .replaceAll(remoteAddress, "http://localhost:" + localPort).getBytes("UTF-8"));
//                         buffer = Unpooled.wrappedBuffer( response.toString().getBytes("UTF-8"));
        	
        	buffer =  Unpooled.wrappedBuffer(buf);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                buffer);
        HttpHeaders.setContentLength(httpResponse, buffer.readableBytes());
        HttpHeaders.setHeader(httpResponse, HttpHeaders.Names.CONTENT_TYPE,
                con.getHeaderField(HttpHeaders.Names.CONTENT_TYPE));
        return httpResponse;
    }

    /*public void startServer() {
        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withPort(8080)
                        .withFiltersSource(new HttpFiltersSourceAdapter() {
                            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                                return new HttpFiltersAdapter(originalRequest) {
                                    @Override
                                    public HttpResponse clientToProxyRequest(HttpObject httpObject) {

                                        if (originalRequest.getUri().contains("favicon")){
                                            return null;
                                        }

                                        System.out.println(originalRequest);
                                        // TODO: implement your filtering here
                                        return null;
                                    }

                                    @Override
                                    public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                        // TODO: implement your filtering here
                                        return httpObject;
                                    }
                                };
                            }
                        })
                        .start();
    }*/
}
