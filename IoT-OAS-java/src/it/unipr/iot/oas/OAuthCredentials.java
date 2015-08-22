package it.unipr.iot.oas;

import java.util.ArrayList;

/**
 * 
 *
 * @author	Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class OAuthCredentials {

	private OAuthConsumer consumer;
	private OAuthToken token;
	private ArrayList<Resource> resources;

	public OAuthCredentials() {
	}

	public OAuthCredentials(OAuthConsumer consumer, OAuthToken token, ArrayList<Resource> resources) {
		this.consumer = consumer;
		this.token = token;
		this.resources = resources;
	}

	public OAuthConsumer getConsumer() {
		return this.consumer;
	}

	public void setConsumer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public OAuthToken getToken() {
		return this.token;
	}

	public void setToken(OAuthToken token) {
		this.token = token;
	}

	public ArrayList<Resource> getResources() {
		return this.resources;
	}

	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nConsumer: ");
		sb.append(this.consumer.toString());
		sb.append("\nToken: ");
		sb.append(this.token.toString());
		sb.append("\nResources:\n");
		for (Resource r : this.resources){
			sb.append("\t" + r.toString());
		}
		return new String(sb);
	}
	
}
