package it.unipr.iot.oas;

/**
 * This interface defines methods to start or stop a generic HttpServer
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public interface IHTTPServer {

	public void start() throws HTTPServerException;

	public void stop() throws HTTPServerException;
	
}
