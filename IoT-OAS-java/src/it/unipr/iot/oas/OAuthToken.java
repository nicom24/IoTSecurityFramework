package it.unipr.iot.oas;

/**
 * 
 *
 * @author	Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OAuthToken {

	private String token;
	private String secret;
	private String type;
	private String expires;

	public OAuthToken() {
	}

	public OAuthToken(String token, String secret, String type, String expires) {
		this.token = token;
		this.secret = secret;
		this.type = type;
		this.expires = expires;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExpires() {
		return this.expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String toString() {
		return "Token: " + this.type + " [" + this.token + "," + this.secret + "] - Expires: " + this.expires;
	}
	
}
