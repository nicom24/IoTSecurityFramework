package it.unipr.iot.oas;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public interface IOAuthStore {

	public OAuthCredentials getOAuthCredentials(String consumerKey, String accessToken);
	
}
