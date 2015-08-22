package it.unipr.iot.oas.process;

import it.unipr.iot.oas.BasicOAuthSignatureVerifier;
import it.unipr.iot.oas.IHTTPServer;
import it.unipr.iot.oas.IProtocol;
import it.unipr.iot.oas.JettyHTTPServer;
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

public class OASLauncher {

	private static Logger logger = LoggerFactory.getLogger(OASLauncher.class);

	/**
	 * @param args, not used
	 */
	public static void main(String[] args) {

		String clazz = OASLauncher.class.getCanonicalName();

		int httpPort = 8080;
		String oasStoreURL = "http://localhost/iot-oas/index.php/oas/fetch";

		BasicOAuthSignatureVerifier verifier = new BasicOAuthSignatureVerifier();

		RESTOAuthStore store = new RESTOAuthStore(oasStoreURL);
		verifier.setOAuthStore(store);

		AbstractHandler handler = new IoTOASHTTPHandler(IProtocol.HTTP, verifier);

		IHTTPServer httpServer = new JettyHTTPServer(httpPort, handler);
		OAS oas = new OAS();
		oas.addServer(httpServer);

		try{
			oas.startAllServers();
		} catch (Exception e){
			logger.error(e.getMessage());
		}

	}
}
