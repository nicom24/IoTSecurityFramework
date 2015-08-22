package it.unipr.iot.oas.proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.logging.Level;

import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoAPEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.proxy.DirectProxyCoAPResolver;
import org.eclipse.californium.proxy.ProxyHttpServer;
import org.eclipse.californium.proxy.resources.ForwardingResource;
import org.eclipse.californium.proxy.resources.ProxyCoapClientResource;
import org.eclipse.californium.proxy.resources.ProxyHttpClientResource;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.eclipse.californium.tools.ResourceDirectory;

public class OASCoapProxy {

	//Log init
		static{
			CaliforniumLogger.initialize();
			CaliforniumLogger.setLevel(Level.INFO);
			ScandiumLogger.initialize();
			ScandiumLogger.setLevel(Level.INFO);
		}
		
		private static final int PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_SECURE_PORT);
		public static final int HTTP_PORT = 9080;
		public static final int HTTPS_PORT = 9443;
		
		//Endpoint for DTLS
		private static Endpoint dtlsEndpoint;
		private DTLSConnector connector;
		
		public OASCoapProxy() throws IOException{
			ForwardingResource coap2coap = new ProxyCoapClientResource("coap2coap");
			ForwardingResource coap2http = new ProxyHttpClientResource("coap2http");
			OASProxyHttpServer httpsServer = new OASProxyHttpServer(HTTPS_PORT);
			httpsServer.setProxyCoapResolver(new DirectProxyCoAPResolver(coap2coap));
			OASProxyHttpServer httpServer = new OASProxyHttpServer(HTTP_PORT);
			httpServer.setProxyCoapResolver(new DirectProxyCoAPResolver(coap2coap));
			//DTLS implementation
			loadStores();
			//Set secure endpoint
			EndpointManager.getEndpointManager().setDefaultSecureEndpoint(dtlsEndpoint);
		}
		
		private void loadStores(){
			try{
				//Load trust store
				KeyStore trustStore = KeyStore.getInstance("JKS");
				InputStream inTrust = new FileInputStream(TRUST_STORE_LOCATION);
				trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray());
				//Load certificate
				Certificate[] trustedCertificates = new Certificate[1];
				trustedCertificates[0] = trustStore.getCertificate(CERTIFICATE_IDENTITY);
				//Create connector
				connector = new DTLSConnector(new InetSocketAddress(0), trustedCertificates);
				KeyStore keyStore = KeyStore.getInstance("JKS");
				InputStream in = new FileInputStream(KEY_STORE_LOCATION);
				keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
				connector.getConfig().setPrivateKey((PrivateKey)keyStore.getKey(KEYSTORE_IDENTITY, 
						KEY_STORE_PASSWORD.toCharArray()), keyStore.getCertificateChain(KEYSTORE_IDENTITY), true);
				dtlsEndpoint = new CoAPEndpoint(connector, NetworkConfig.getStandard());
				dtlsEndpoint.start();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private static final String CERTIFICATE_IDENTITY = "root";
		private static final String KEYSTORE_IDENTITY = "client";
		private static final String TRUST_STORE_PASSWORD = "rootPass";
		private static final String KEY_STORE_PASSWORD = "endPass";
		private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
		private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
		
		public static void main(String[] args)throws Exception{
			new OASCoapProxy();
			CoapServer rd = new ResourceDirectory();
			rd.start();
			System.out.printf(ResourceDirectory.class.getSimpleName()+" listening on port %d.\n", rd.getEndpoints().get(0).getAddress().getPort());
		}
	
}
