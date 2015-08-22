package it.unipr.iot.oas.process;

import it.unipr.iot.oas.BasicOAuthSignatureVerifier;
import it.unipr.iot.oas.IHTTPServer;
import it.unipr.iot.oas.IProtocol;
import it.unipr.iot.oas.JettyHTTPSServer;
import it.unipr.iot.oas.OAS;
import it.unipr.iot.oas.RESTOAuthStore;
import it.unipr.iot.oas.framework.IoTOASHTTPHandler;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OASLauncherSSL {

	private static Logger logger = LoggerFactory.getLogger(OASLauncherSSL.class);
	
	public static final String OASurl = "https://localhost:8443";
	//Localhost OAS
	//public static final String oasStoreURL = "http://localhost/iot-oas/index.php/oas/fetch";
	//Remote OAS
	public static final String oasStoreURL = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/fetch";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String clazz = OASLauncherSSL.class.getCanonicalName();

		/*if(args.length < 7){
			logger.error("usage: {} localHttpPort oasIp oasPort oasStoreURL keyStorePath keyStorePassword keyManagerPassword", clazz);
			System.exit(1);
		}
		int httpsPort = Integer.parseInt(args[0]);
		String proxyIp = args[1];
		int proxyPort = Integer.parseInt(args[2]);
		String oasStoreURL = args[3];
		String keyStorePath = args[4];
		String keyStorePassword = args[5];
		String keyManagerPassword = args[6];*/
		String keyStorePath = "SSLKeyStore/keystore.jks";
		String keyStorePassword = "";
		String keyManagerPassword = "";

		int httpsPort = 8443;
		

		BasicOAuthSignatureVerifier verifier = new BasicOAuthSignatureVerifier();

		RESTOAuthStore store = new RESTOAuthStore(oasStoreURL);
		verifier.setOAuthStore(store);

		AbstractHandler handler = new IoTOASHTTPHandler(IProtocol.HTTPS, verifier);

		IHTTPServer httpSSLServer = new JettyHTTPSServer(httpsPort, keyStorePath, keyStorePassword, keyManagerPassword, handler);
		OAS oas = new OAS();
		oas.addServer(httpSSLServer);

		try{
			oas.startAllServers();
		} catch (Exception e){
			logger.error(e.getMessage());
		}

	}
	
}
