package it.unipr.iot.oas;

import it.unipr.iot.oas.util.RESTRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class RESTOAuthStore implements IOAuthStore {

	private static Logger logger = LoggerFactory.getLogger(RESTOAuthStore.class);

	private String serviceURL;

	public RESTOAuthStore(String serviceURL) {
		if(serviceURL.endsWith("/")) this.serviceURL = serviceURL.substring(0, serviceURL.length() - 1);
		else this.serviceURL = serviceURL;
	}

	public OAuthCredentials getOAuthCredentials(String consumerKey, String accessToken) {
		String url = this.serviceURL + "/" + consumerKey + "/" + accessToken;
		logger.info("{} Getting OAuth Credentials from {}", new java.util.Date(), url);
		OAuthCredentials credentials = RESTRequest.request(url, OAuthCredentials.class);
		logger.info("{} OAuth Credentials: {}", new java.util.Date(), credentials);
		return credentials;
	}

}
