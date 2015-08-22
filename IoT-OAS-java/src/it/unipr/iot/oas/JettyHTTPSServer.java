package it.unipr.iot.oas;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a standard HTTPS server using Jetty libraries, 
 * implements ssl with various parameters to set (KeyStore, TrustStore)
 *
 * @author Luca Davoli - <a href="mailto:lucadavo@gmail.com">lucadavo@gmail.com</a> - Department of Information Engineering - University of Parma
 *
 */

public class JettyHTTPSServer implements IHTTPServer {

	private static Logger logger = LoggerFactory.getLogger(JettyHTTPSServer.class);

	private Server httpSSLServer;

	public JettyHTTPSServer(int sslPort, String keyStorePath, String keyStorePassword, String keyManagerPassword, AbstractHandler handler) {
		this.httpSSLServer = new Server();

		SslContextFactory sslContextFactory = new SslContextFactory();
		/*sslContextFactory.setKeyStorePath(keyStorePath);
		sslContextFactory.setKeyStorePassword(keyStorePassword);
		sslContextFactory.setKeyManagerPassword(keyManagerPassword);*/

		sslContextFactory.setKeyStorePath("SSLKeyStore/keystore.jks");
		sslContextFactory.setKeyStorePassword("123456789");
		sslContextFactory.setKeyManagerPassword("123456789");

		/*sslContextFactory.setTrustStore("SSLKeyStore/keystore.jks");
		sslContextFactory.setTrustStorePassword("123456789");
		sslContextFactory.setNeedClientAuth(true);*/

		SslSocketConnector connector = new SslSocketConnector(sslContextFactory);
		connector.setPort(sslPort);

		this.httpSSLServer.setConnectors(new Connector[] { connector });
		this.httpSSLServer.setHandler(handler);
	}

	public void start() throws HTTPServerException {
		logger.debug("Starting HTTPS server");
		if(!this.httpSSLServer.isStarted()){
			try{
				this.httpSSLServer.start();
				this.httpSSLServer.join();
			} catch (Exception e){
				throw new HTTPServerException(e.getMessage());
			}
		}
		logger.debug("HTTPS server started");
	}

	public void stop() throws HTTPServerException {
		logger.debug("Stopping HTTPS server");
		if(!this.httpSSLServer.isStopped()){
			try{
				this.httpSSLServer.stop();
				this.httpSSLServer.join();
			} catch (Exception e){
				throw new HTTPServerException(e.getMessage());
			}
		}
		logger.debug("HTTPS server stopped");
	}
	
}