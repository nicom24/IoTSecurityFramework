package it.unipr.iot.oas.proxy;

import it.unipr.iot.oas.framework.IoTOASRequest;
import it.unipr.iot.oas.process.OASLauncherSSL;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerRegistry;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.proxy.HttpTranslator;
import org.eclipse.californium.proxy.InvalidFieldException;
import org.eclipse.californium.proxy.InvalidMethodException;
import org.eclipse.californium.proxy.RequestHandler;
import org.eclipse.californium.proxy.TranslationException;

import com.google.gson.Gson;

/**
 * Class encapsulating the logic of a http server. The class create a receiver
 * thread that it is always blocked on the listen primitive. For each connection
 * this thread creates a new thread that handles the client/server dialog.
 */

public class OASHttpStack {

private static final Logger LOGGER = Logger.getLogger(OASHttpStack.class.getCanonicalName());
	
	private static final Response Response_NULL = new Response(null); // instead of Response.NULL // TODO
	
	private static final int SOCKET_TIMEOUT = NetworkConfig.getStandard().getInt(
			NetworkConfig.Keys.HTTP_SERVER_SOCKET_TIMEOUT);
	private static final int SOCKET_BUFFER_SIZE = NetworkConfig.getStandard().getInt(
			NetworkConfig.Keys.HTTP_SERVER_SOCKET_BUFFER_SIZE);
	private static final int GATEWAY_TIMEOUT = SOCKET_TIMEOUT * 3 / 4;
	private static final String SERVER_NAME = "Californium Http Proxy";
	
	//Certificates and keystore info
	private static final String TRUST_STORE_PASSWORD = "rootPass";
	private static final String KEY_STORE_PASSWORD = "endPass";
	private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
	private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
	
	/**
	 * Resource associated with the proxying behavior. If a client requests
	 * resource indicated by
	 * http://proxy-address/PROXY_RESOURCE_NAME/coap-server, the proxying
	 * handler will forward the request desired coap server.
	 */
	private static final String PROXY_RESOURCE_NAME = "proxy";

	/**
	 * The resource associated with the local resources behavior. If a client
	 * requests resource indicated by
	 * http://proxy-address/LOCAL_RESOURCE_NAME/coap-resource, the proxying
	 * handler will forward the request to the local resource requested.
	 */
	public static final String LOCAL_RESOURCE_NAME = "local";
	
	/**
	 * Defaul https port 
	 */
	public static final int HTTPS_PORT = 8443;

	private final ConcurrentHashMap<Request, Exchanger<Response>> exchangeMap = new ConcurrentHashMap<Request, Exchanger<Response>>();

	private RequestHandler requestHandler;
	
	/**
	 * Instantiates a new http stack on the requested port. It creates an http
	 * listener thread on the port.
	 * 
	 * @param httpPort
	 *            the http port
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OASHttpStack(int httpPort) throws IOException {
		new HttpServer(httpPort);
	}

	public static SSLContext getStdSSLContext(){
		//Load trust store
		try{
			//Load trust store
			KeyStore trustStore = KeyStore.getInstance("jks");
			InputStream inTrust = new FileInputStream(TRUST_STORE_LOCATION);
			trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray());
			TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmFactory.init(trustStore);
			TrustManager[] trustmanagers = tmFactory.getTrustManagers();
			//Load key store
			KeyStore keystore = KeyStore.getInstance("jks");
			InputStream inStore = new FileInputStream(KEY_STORE_LOCATION);
			keystore.load(inStore, KEY_STORE_PASSWORD.toCharArray());
			KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmFactory.init(keystore, KEY_STORE_PASSWORD.toCharArray());
			KeyManager[] keymanagers = kmFactory.getKeyManagers();
			//Init SSL
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(keymanagers, trustmanagers, null);
			return sslcontext;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Checks if a thread is waiting for the arrive of a specific response.
	 * 
	 * @param request
	 *            the request
	 * @return true, if is waiting
	 */
	public boolean isWaitingRequest(Request request) {

		// DEBUG
		// System.out.println(request.hashCode());
		// request.prettyPrint();
		//
		// System.out.println(responseMap.get(request) != null);
		// System.out.println(semaphoreMap.get(request) != null);
		//
		// for (Request r : responseMap.keySet()) {
		// System.out.println(r.hashCode());
		// r.prettyPrint();
		// }
		//
		// for (Request r : semaphoreMap.keySet()) {
		// System.out.println(r.hashCode());
		// r.prettyPrint();
		// }

		// check the presence of the key in both maps
		// TODO check how much is this operation heavy
		// return responseMap.containsKey(request) &&
		// semaphoreMap.containsKey(request);

		return exchangeMap.containsKey(request);
	}

	/**
	 * Send simple http response.
	 * 
	 * @param httpExchange
	 *            the http exchange
	 * @param httpCode
	 *            the http code
	 */
	private void sendSimpleHttpResponse(HttpAsyncExchange httpExchange, int httpCode) {
		// get the empty response from the exchange
		HttpResponse httpResponse = httpExchange.getResponse();

		// create and set the status line
		StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, httpCode, EnglishReasonPhraseCatalog.INSTANCE.getReason(httpCode, Locale.ENGLISH));
		httpResponse.setStatusLine(statusLine);

		// send the error response
		httpExchange.submitResponse();
	}

	protected void doSendResponse(Request request, Response response) throws IOException {
		// the http stack is intended to send back only coap responses

		// retrieve the request linked to the response
//		if (Bench_Help.DO_LOG) 
			LOGGER.fine("Handling response for request: " + request);

		// fill the exchanger with the incoming response
		Exchanger<Response> exchanger = exchangeMap.get(request);
		if (exchanger != null) {
			try {
				exchanger.exchange(response);
//				if (Bench_Help.DO_LOG) 
					LOGGER.info("Exchanged correctly");
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Exchange interrupted", e);

				// remove the entry from the map
				exchangeMap.remove(request);
				return;
			}
		} else {
			LOGGER.warning("exchanger was null for request "+request+" with hash "+request.hashCode());
		}
	}

	/**
	 * The Class CoapResponseWorker. This thread waits a response from the lower
	 * layers. It is the consumer of the producer/consumer pattern.
	 */
	private final class CoapResponseWorker extends Thread {
		private final HttpAsyncExchange httpExchange;
		private final HttpRequest httpRequest;
		private final Request coapRequest;

		/**
		 * Instantiates a new coap response worker.
		 * 
		 * @param name
		 *            the name
		 * @param coapRequest
		 *            the coap request
		 * @param httpExchange
		 *            the http exchange
		 * @param httpRequest
		 *            the http request
		 */
		public CoapResponseWorker(String name, Request coapRequest, HttpAsyncExchange httpExchange, HttpRequest httpRequest) {
			super(name);
			this.coapRequest = coapRequest;
			this.httpExchange = httpExchange;
			this.httpRequest = httpRequest;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// get the exchanger
			Exchanger<Response> exchanger = exchangeMap.get(coapRequest);

			// if the map does not contain the key, send an error response
			if (exchanger == null) {
				LOGGER.warning("exchanger == null");
				sendSimpleHttpResponse(httpExchange, HttpStatus.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			// get the response
			Response coapResponse = null;
			try {
				coapResponse = exchanger.exchange(Response_NULL, GATEWAY_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				LOGGER.warning("Timeout occurred");
				// send the timeout error message
				sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_TIMEOUT);
				return;
			} catch (InterruptedException e) {
				// if the thread is interrupted, terminate
				if (isInterrupted()) {
					LOGGER.warning("Thread interrupted");
					sendSimpleHttpResponse(httpExchange, HttpStatus.SC_INTERNAL_SERVER_ERROR);
					return;
				}
			} finally {
				// remove the entry from the map
				exchangeMap.remove(coapRequest);
//				if (Bench_Help.DO_LOG) 
					LOGGER.finer("Entry removed from map");
			}

			if (coapResponse == null) {
				LOGGER.warning("No coap response");
				sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_NOT_FOUND);
				return;
			}

			// get the sample http response
			HttpResponse httpResponse = httpExchange.getResponse();

			try {
				// translate the coap response in an http response
				HttpTranslator.getHttpResponse(httpRequest, coapResponse, httpResponse);

//				if (Bench_Help.DO_LOG) 
					LOGGER.finer("Outgoing http response: " + httpResponse.getStatusLine());
			} catch (TranslationException e) {
				LOGGER.warning("Failed to translate coap response to http response: " + e.getMessage());
				sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_TRANSLATION_ERROR);
				return;
			}

			// send the response
			httpExchange.submitResponse();
		}
	}

	private class HttpServer {

		public HttpServer(int httpPort) {
			// HTTP parameters for the server
			HttpParams params = new SyncBasicHttpParams();
			params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, SOCKET_BUFFER_SIZE).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, SERVER_NAME);

			// Create HTTP protocol processing chain
			// Use standard server-side protocol interceptors
			HttpRequestInterceptor[] requestInterceptors = new HttpRequestInterceptor[] { new RequestAcceptEncoding() };
			HttpResponseInterceptor[] responseInterceptors = new HttpResponseInterceptor[] { new ResponseContentEncoding(), new ResponseDate(), new ResponseServer(), new ResponseContent(), new ResponseConnControl() };
			HttpProcessor httpProcessor = new ImmutableHttpProcessor(requestInterceptors, responseInterceptors);

			// Create request handler registry
			HttpAsyncRequestHandlerRegistry registry = new HttpAsyncRequestHandlerRegistry();

			// register the handler that will reply to the proxy requests
			registry.register("/" + PROXY_RESOURCE_NAME + "/*", new ProxyAsyncRequestHandler(PROXY_RESOURCE_NAME, true));
			// register the handler for the frontend
			registry.register("/" + LOCAL_RESOURCE_NAME + "/*", new ProxyAsyncRequestHandler(LOCAL_RESOURCE_NAME, false));
			// register the default handler for root URIs
			// wrapping a common request handler with an async request handler
			registry.register("*", new BasicAsyncRequestHandler(new BaseRequestHandler()));

			// Create server-side HTTP protocol handler
			HttpAsyncService protocolHandler = new HttpAsyncService(httpProcessor, new DefaultConnectionReuseStrategy(), registry, params);

			// Create HTTP connection factory
			NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory;

			//Check if SSL connection
			if (httpPort==OASCoapProxy.HTTPS_PORT){
				try{
					LOGGER.info("Setting up SSL connection for https://");
					// Initialize SSL context
					
					connFactory = new SSLNHttpServerConnectionFactory(getStdSSLContext(), null, params);
				}catch(Exception e){
					e.printStackTrace();
					connFactory = new DefaultNHttpServerConnectionFactory(params); 
				}
			}else{
				//Not an SSL connection, use default factory
				connFactory = new DefaultNHttpServerConnectionFactory(params); 
			}
			// Create server-side I/O event dispatch
			final IOEventDispatch ioEventDispatch = new DefaultHttpServerIODispatch(protocolHandler, connFactory);

			final ListeningIOReactor ioReactor;
			try {
				// Create server-side I/O reactor
				ioReactor = new DefaultListeningIOReactor();
				// Listen of the given port
				LOGGER.info("HttpStack listening on port "+httpPort);
				ioReactor.listen(new InetSocketAddress(httpPort));

				// create the listener thread
				Thread listener = new Thread("HttpStack listener") {

					@Override
					public void run() {
						// Starts the reactor and initiates the dispatch of I/O
						// event notifications to the given IOEventDispatch.
						try {
							LOGGER.info("Submitted http listening to thread 'HttpStack listener'");

							ioReactor.execute(ioEventDispatch);
						} catch (IOException e) {
							LOGGER.severe("Interrupted");
						}

						LOGGER.info("Shutdown HttpStack");
					}
				};

				listener.setDaemon(false);
				listener.start();
				LOGGER.info("HttpStack started");
			} catch (IOException e) {
				LOGGER.severe("I/O error: " + e.getMessage());
			}
		}

		/**
		 * The Class BaseRequestHandler handles simples requests that do not
		 * need the proxying.
		 */
		private class BaseRequestHandler implements HttpRequestHandler {

			/*
			 * (non-Javadoc)
			 * @see
			 * org.apache.http.protocol.HttpRequestHandler#handle(org.apache
			 * .http .HttpRequest, org.apache.http.HttpResponse,
			 * org.apache.http.protocol.HttpContext)
			 */
			@Override
			public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
				httpResponse.setStatusCode(HttpStatus.SC_OK);
				httpResponse.setEntity(new StringEntity("Californium Proxy server"));

//				if (Bench_Help.DO_LOG) 
					LOGGER.finer("Root request handled");
			}
		}

		/**
		 * Class associated with the http service to translate the http requests
		 * in coap requests and to produce the http responses. Even if the class
		 * accepts a string indicating the name of the proxy resource, it is
		 * still thread-safe because the local resource is set in the
		 * constructor and then only read by the methods.
		 */
		private class ProxyAsyncRequestHandler implements
				HttpAsyncRequestHandler<HttpRequest> {

			private final String localResource;
			private final boolean proxyingEnabled;

			/**
			 * Instantiates a new proxy request handler.
			 * 
			 * @param localResource
			 *            the local resource
			 * @param proxyingEnabled
			 */
			public ProxyAsyncRequestHandler(String localResource, boolean proxyingEnabled) {
				super();

				this.localResource = localResource;
				this.proxyingEnabled = proxyingEnabled;
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * org.apache.http.nio.protocol.HttpAsyncRequestHandler#handle(java.
			 * lang.Object, org.apache.http.nio.protocol.HttpAsyncExchange,
			 * org.apache.http.protocol.HttpContext)
			 */
			@Override
			public void handle(HttpRequest httpRequest, HttpAsyncExchange httpExchange, HttpContext httpContext) throws HttpException, IOException {
//				if (Bench_Help.DO_LOG) 
					LOGGER.finer("Incoming http request: " + httpRequest.getRequestLine());
					/*
					 * OAS request interceptor
					 */
					String reqLine =  httpRequest.getRequestLine().getUri();
					if (reqLine.startsWith("/proxy/coap://localhost") || reqLine.startsWith("/proxy/coaps://localhost")){
						handleVerified(httpRequest, httpExchange, httpContext);
						System.out.println("Request on the proxy itself.");
						return;
					}
					//Check if authorization header is present
					if (!httpRequest.containsHeader("Authorization")){
						sendSimpleHttpResponse(httpExchange, HttpStatus.SC_UNAUTHORIZED);
						return;
					}
					IoTOASRequest oasRequest = new IoTOASRequest();
					oasRequest.setMethod(httpRequest.getRequestLine().getMethod());
					System.out.println("Proxy Method: " + oasRequest.getMethod());
					String authHeader = httpRequest.getFirstHeader("Authorization").getValue();
					oasRequest.setAuthorizationHeader(authHeader);
					String url;
					if(httpRequest.getFirstHeader("Host").getValue().matches("(.*)" + OASCoapProxy.HTTP_PORT)){
						//Https
						LOGGER.info("Request using http");
						url = "http://" + httpRequest.getFirstHeader("Host").getValue() +httpRequest.getRequestLine().getUri();
					}else{
						LOGGER.info("Request using http + TLS");
						url = "https://" + httpRequest.getFirstHeader("Host").getValue() +httpRequest.getRequestLine().getUri();
					}
					/*try{
						oasRequest.setUrl(new URL(url));
					}catch(MalformedURLException e){
						e.printStackTrace();
						sendSimpleHttpResponse(httpExchange, HttpStatus.SC_BAD_REQUEST);
					}*/
					//Choose the hardcoded uuid translator
					HttpRequest proxiedRequest = null;
					String uuid = null;
					IUrlUuidTranslator uuidFinder = UuidHardcodedTranslator.getInstance();
					String proxiedUrl = url.substring(url.indexOf("proxy/")+"proxy/".length());
					if (proxiedUrl.startsWith("coap://")||proxiedUrl.startsWith("coaps://")){
						proxiedRequest = httpRequest;
						System.out.println("URL: " + proxiedUrl);
						if ((uuid = uuidFinder.UuidFromUrl(proxiedUrl))==null){
							//Uuid not found
							System.out.println("uuid: " + uuid);
							sendSimpleHttpResponse(httpExchange, HttpStatus.SC_NOT_FOUND);
							return;
						}
					}else{
						//Translate request
						uuid = proxiedUrl;
						String urler;
						if ((urler = uuidFinder.UrlFromUuid(proxiedUrl))==null){
							//Uuid not found
							sendSimpleHttpResponse(httpExchange, HttpStatus.SC_NOT_FOUND);
							return;
						}else{
							//Root with uuid
							String newRequestUrl = "/proxy/" + urler;
							if (httpRequest instanceof HttpEntityEnclosingRequest){
								proxiedRequest = new BasicHttpEntityEnclosingRequest(httpRequest.getRequestLine().getMethod(),
										newRequestUrl,httpRequest.getProtocolVersion());
								((HttpEntityEnclosingRequest)proxiedRequest).setEntity(
										((HttpEntityEnclosingRequest)httpRequest).getEntity());
							}else{
								proxiedRequest = new BasicHttpRequest(httpRequest.getRequestLine().getMethod(),
										newRequestUrl,httpRequest.getProtocolVersion());
							}
							proxiedRequest.setHeaders(httpRequest.getAllHeaders());
						}
					}
					//Inet4Address.getLocalHost().getHostAddress()
					LOGGER.info("Auth:" + oasRequest.getAuthorizationHeader());
					oasRequest.setUuid(uuid);
					//Create client and ask for authorization
					try{
						Gson gson = new Gson();
						HttpClient client = verifiedClient();
						HttpPost clientRequest = new HttpPost(OASLauncherSSL.OASurl);
						clientRequest.addHeader("Content-Type", IoTOASRequest.HTTP_CONTENT_TYPE);
						clientRequest.setEntity(new StringEntity(gson.toJson(oasRequest)));
						HttpResponse rx = client.execute(clientRequest);
						LOGGER.info("Sent request to OAS");
						if (rx.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
							//Right request
							LOGGER.info("Client is authorized to perfom request");
							handleVerified(proxiedRequest, httpExchange, httpContext);
						}else{
							sendSimpleHttpResponse(httpExchange,rx.getStatusLine().getStatusCode()) ;
						}
					}catch(Exception e){
						e.printStackTrace();
						sendSimpleHttpResponse(httpExchange,HttpStatus.SC_INTERNAL_SERVER_ERROR) ;
					}
				}
			
			private HttpClient verifiedClient(){
				try{
					HttpClient client = null;
					SSLContext c = OASHttpStack.getStdSSLContext();
					TrustStrategy acceptAllStrategy = new TrustSelfSignedStrategy();
					org.apache.http.conn.ssl.SSLSocketFactory sf = new org.apache.http.conn.ssl.SSLSocketFactory(acceptAllStrategy, 
							org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
					SchemeRegistry registry = new SchemeRegistry();
					registry.register(new Scheme("https", 8443, sf));
					ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
					client = new DefaultHttpClient(ccm);
					return client;
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
			
			/*
			 * Called when a request is authorized from OAS service, old handle from californium
			 */
			public void handleVerified(HttpRequest httpRequest, HttpAsyncExchange httpExchange, HttpContext httpContext) throws HttpException, IOException {
				try {
					// translate the request in a valid coap request
					Request coapRequest = HttpTranslator.getCoapRequest(httpRequest, localResource, proxyingEnabled);
//					if (Bench_Help.DO_LOG) 
						LOGGER.info("Received HTTP request and translate to "+coapRequest);

					// fill the maps
					exchangeMap.put(coapRequest, new Exchanger<Response>());
//					if (Bench_Help.DO_LOG) 
						LOGGER.finer("Fill exchange with: " + coapRequest+" with hash="+coapRequest.hashCode());

					// the new thread will wait for the completion of
					// the coap request
					Thread worker = new CoapResponseWorker("HttpStack Worker", coapRequest, httpExchange, httpRequest);

					// starting the "consumer thread" that will sleep waiting
					// for the producer
					worker.start();
//					if (Bench_Help.DO_LOG) 
						LOGGER.finer("Started thread 'httpStack worker' to wait the response");

					// send the coap request to the upper layers
					doReceiveMessage(coapRequest);
				} catch (InvalidMethodException e) {
					LOGGER.warning("Method not implemented" + e.getMessage());
					sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_WRONG_METHOD);
					return;
				} catch (InvalidFieldException e) {
					LOGGER.warning("Request malformed" + e.getMessage());
					sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_URI_MALFORMED);
					return;
				} catch (TranslationException e) {
					LOGGER.warning("Failed to translate the http request in a valid coap request: " + e.getMessage());
					sendSimpleHttpResponse(httpExchange, HttpTranslator.STATUS_TRANSLATION_ERROR);
					return;
				} catch (RuntimeException e) {
					LOGGER.warning("Exception in translation: "+e);
					e.printStackTrace();
					throw e;
				}
				
			}
			/*
			 * (non-Javadoc)
			 * @see
			 * org.apache.http.nio.protocol.HttpAsyncRequestHandler#processRequest
			 * (org.apache.http.HttpRequest,
			 * org.apache.http.protocol.HttpContext)
			 */
			@Override
			public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
				// Buffer request content in memory for simplicity
				return new BasicAsyncRequestConsumer();
			}
		}

	}
	
	public void doReceiveMessage(Request request) {
		requestHandler.handleRequest(request);
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}
	
}
