package it.unipr.iot.oas;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a standard HTTP server using Jetty libraries
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class JettyHTTPServer implements IHTTPServer {

	private static Logger logger = LoggerFactory.getLogger(JettyHTTPServer.class);

	private Server httpServer;

	public JettyHTTPServer(int port, AbstractHandler handler) {
		this.httpServer = new Server(port);
		this.httpServer.setHandler(handler);
	}

	public void start() throws HTTPServerException {
		logger.debug("Starting server");
		if(!this.httpServer.isStarted()){
			try{
				this.httpServer.start();
				this.httpServer.join();
			} catch (Exception e){
				throw new HTTPServerException(e.getMessage());
			}
		}
		logger.debug("Server started");
	}

	public void stop() throws HTTPServerException {
		logger.debug("Stopping server");
		if(!this.httpServer.isStopped()){
			try{
				this.httpServer.stop();
				this.httpServer.join();
			} catch (Exception e){
				throw new HTTPServerException(e.getMessage());
			}
		}
		logger.debug("Server stopped");
	}
	
}
