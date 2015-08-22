package it.unipr.iot.oas;

/**
 * 
 *
 * @author	Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OAuthConsumer {

	private String key;
	private String secret;
	private String name;

	public OAuthConsumer() {
	}

	public OAuthConsumer(String key, String secret, String name) {
		this.key = key;
		this.secret = secret;
		this.name = name;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "Consumer: " + this.name + " [" + this.key + "," + this.secret + "]";
	}
	
}
