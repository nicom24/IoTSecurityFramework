package it.unipr.iot.oas;

/**
 * This class defines Exceptions for OAS
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OASException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3918582457197449809L;

	public static final int CLIENT_ERROR = 1000;
	public static final int SERVER_ERROR = 2000;

	private int code;
	private String message;

	public OASException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

}
