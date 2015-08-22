package it.unipr.iot.oas;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class HTTPServerException extends Exception {

	private static final long serialVersionUID = -7500239048835147075L;
	private String message;

	public HTTPServerException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
