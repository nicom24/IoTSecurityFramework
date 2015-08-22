package it.unipr.iot.oas;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages a list of IHTTPServers implementors with method to start or
 * stop them all or individually
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OAS {

	private static Logger logger = LoggerFactory.getLogger(OAS.class);

	//private IHTTPServer httpServer;
	private Vector<IHTTPServer> server;

	/*
	public OAS(IHTTPServer httpServer) {
		this.httpServer = httpServer;
	}
	*/
	
	public OAS() {
		this.server = new Vector<IHTTPServer>();
	}
	
	public void addServer(IHTTPServer server) {
		if (this.server != null) {
			this.server.add(server);
		}
	}
	
	public void removeServer(int serverIdx) {
		if (this.server != null) {
			this.server.remove(serverIdx);
		}
	}
	
	public void removeServer(IHTTPServer server) {
		if (this.server != null) {
			this.server.remove(server);
		}
	}
	
	public void startAllServers() throws OASException {
		if (this.server != null) {
			for (IHTTPServer it : this.server) {
				logger.info("Starting OAS " + it.getClass().getName() + " interface");
				try {
					it.start();
				}
				catch (HTTPServerException e) {
					throw new OASException(OASException.SERVER_ERROR, e.getMessage());
				}
				logger.info("Started OAS " + it.getClass().getName() + " interface");
			}
		}
		else {
			throw new OASException(OASException.SERVER_ERROR, "No servers defined");
		}
	}
	
	public void stopAllServers() throws OASException {
		if(this.server != null) {
			for (IHTTPServer it : this.server) {
				logger.info("Stopping OAS " + it.getClass().getName() + " interface");
				try {
					it.stop();
				} catch (HTTPServerException e){
					throw new OASException(OASException.SERVER_ERROR, e.getMessage());
				}
				logger.info("Stopped OAS " + it.getClass().getName() + " interface");
			}
		}
	}
	
	public void startServer(IHTTPServer server) throws OASException {
		if ((this.server != null) && (this.server.indexOf(server) != -1)) {
			logger.info("Starting OAS " + server.getClass().getName() + " interface");
			try {
				this.server.get(this.server.indexOf(server)).start();
			}
			catch (HTTPServerException e) {
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Started OAS " + server.getClass().getName() + " interface");
		}
	}
	
	public void startServer(int serverIdx) throws OASException {
		if ((this.server != null) && (this.server.size() > serverIdx)) {
			logger.info("Starting OAS " + this.server.get(serverIdx).getClass().getName() + " interface");
			try {
				this.server.get(serverIdx).start();
			}
			catch (HTTPServerException e) {
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Started OAS " + this.server.get(serverIdx).getClass().getName() + " interface");
		}
	}
	
	public void stopServer(IHTTPServer server) throws OASException {
		if ((this.server != null) && (this.server.indexOf(server) != -1)) {
			logger.info("Stopping OAS " + server.getClass().getName() + " interface");
			try {
				this.server.get(this.server.indexOf(server)).stop();
			}
			catch (HTTPServerException e) {
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Stopped OAS " + server.getClass().getName() + " interface");
		}
	}
	
	public void stopServer(int serverIdx) throws OASException {
		if ((this.server != null) && (this.server.size() > serverIdx)) {
			logger.info("Stopping OAS " + this.server.get(serverIdx).getClass().getName() + " interface");
			try {
				this.server.get(serverIdx).stop();
			}
			catch (HTTPServerException e) {
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Stopped OAS " + this.server.get(serverIdx).getClass().getName() + " interface");
		}
	}

	/*
	public void start() throws OASException {
		if(this.httpServer != null){
			logger.info("Starting OAS HTTP interface");
			try{
				this.httpServer.start();
			} catch (HTTPServerException e){
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Started OAS HTTP interface");
		}
		else{
			throw new OASException(OASException.SERVER_ERROR, "No HTTP server defined");
		}
	}
	*/

	/*
	public void stop() throws OASException {
		if(this.httpServer != null){
			logger.info("Stopping OAS HTTP interface");
			try{
				this.httpServer.stop();
			} catch (HTTPServerException e){
				throw new OASException(OASException.SERVER_ERROR, e.getMessage());
			}
			logger.info("Stopped OAS HTTP interface");
		}
	}
	*/
	
}
