package it.unipr.iot.oas;

import it.unipr.iot.oas.util.OAuthSignatureUtil;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class BasicOAuthSignatureVerifier implements IOAuthSignatureVerifier {

	private static Logger logger = LoggerFactory.getLogger(BasicOAuthSignatureVerifier.class);

	private IOAuthStore oauthStore;

	public boolean verify(String method, String uuid, String authorizationHeader) throws OASException {
		if(this.oauthStore == null){
			logger.error("{} No OAuth credentials store has been set");
			throw new OASException(OASException.SERVER_ERROR, "No OAuth credentials store has been set");
		}
		if(!authorizationHeader.startsWith(OAUTH_AUTHORIZATION_HEADER_AUTH_SCHEME)){
			logger.error("{} Invalid auth-scheme in Authorization header:\n{}" + authorizationHeader);
			throw new OASException(OASException.CLIENT_ERROR, "Invalid auth-scheme in Authorization header: " + authorizationHeader);
		}
		logger.debug("{} Verifiying signature: {}", authorizationHeader);
		TreeMap<String, String> params = OAuthSignatureUtil.getParameters(uuid, authorizationHeader);

		String oauthConsumerKey = params.get(OAUTH_PARAMETER_oauth_consumer_key);
		String accessToken = params.get(OAUTH_PARAMETER_oauth_token);
		OAuthCredentials credentials = this.oauthStore.getOAuthCredentials(oauthConsumerKey, accessToken);
		if(credentials == null){
			return false;
		}
		else{
			logger.info("{} Retrieved credentials: {}", new java.util.Date(), new Gson().toJson(credentials));
			String oauthConsumerSecret = credentials.getConsumer().getSecret();
			String accessTokenSecret = credentials.getToken().getSecret();
			if(oauthConsumerSecret == null){
				logger.info("{} No consumer with key {} was found", new java.util.Date(), oauthConsumerKey);
				return false;
			}
			if(accessTokenSecret == null){
				logger.info("{} No such token found {}", new java.util.Date(), accessToken);
				return false;
			}
			if(credentials.getToken().getExpires() != null && credentials.getToken().getExpires().equals("0000-00-00 00:00:00") == false){
				String pattern = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat formatter = new SimpleDateFormat(pattern);
				credentials.getToken().getExpires();
				Date expirationTime;
				try{
					expirationTime = formatter.parse(credentials.getToken().getExpires());
				} catch (ParseException e){
					logger.error(e.getMessage());
					throw new OASException(OASException.SERVER_ERROR, "Date parsing error");
				}
				Date now = new Date();
				if(expirationTime.before(now)){
					logger.info("{} Token has expired on {}", new java.util.Date(), credentials.getToken().getExpires());
					return false;
				}
			}
			logger.info("{} verifying if token is authorized for {} {}", new java.util.Date(), method, uuid);
			ArrayList<Resource> resources = credentials.getResources();
			for(Resource resource : resources){
				if (resource.getUuid().equals(uuid) && resource.getActions().contains(method)){
					logger.debug("{} {}", new java.util.Date(), resource);
					// should verify absolute URI instead of path
					logger.debug("{} {} - {}", new java.util.Date(), uuid);
					//Check if request is authorized for resource
					String oauthSignatureMethod = params.get(OAUTH_PARAMETER_oauth_signature_method);
					String oauthSignature = params.get(OAUTH_PARAMETER_oauth_signature);
					logger.info("{} Original signature[{}] {}", new java.util.Date(), oauthSignatureMethod, oauthSignature);
					String signature = OAuthSignatureUtil.sign(method, uuid, params, oauthConsumerSecret, accessTokenSecret);
					logger.info("{} Computed signature[{}] {}", new java.util.Date(), oauthSignatureMethod, signature);
					if(signature.equals(oauthSignature)) return true;
					else return false;
				}
			}
			return false;
		}
	}

	public void setOAuthStore(IOAuthStore oauthStore) {
		this.oauthStore = oauthStore;
	}

}
