package it.unipr.iot.oas.util;

import it.unipr.iot.oas.IOAuthSignatureVerifier;
import it.unipr.iot.oas.IProtocol;
import it.unipr.iot.oas.OASException;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoolu.tools.Base64;

public class OAuthSignatureUtil {

	private static Logger logger = LoggerFactory.getLogger(OAuthSignatureUtil.class);

	public static String sign(String method, String uuid, String authorizationHeader, String oauthConsumerSecret, String accessTokenSecret) throws OASException {
		TreeMap<String, String> params = OAuthSignatureUtil.getParameters(uuid, authorizationHeader);
		String baseSignatureString = OAuthSignatureUtil.computeBaseSignatureString(method, uuid, params);
		logger.debug("Base Signature String = {}", baseSignatureString);
		String oauthSignatureMethod = params.get(IOAuthSignatureVerifier.OAUTH_PARAMETER_oauth_signature_method);
		//String signature = computeSignature(baseSignatureString, oauthSignatureMethod, oauthConsumerSecret, accessTokenSecret, (url.getProtocol().equals(IProtocol.HTTPS)));
		String signature = computeSignature(baseSignatureString, oauthSignatureMethod, oauthConsumerSecret, accessTokenSecret, true);
		return signature;
	}

	public static String sign(String method, String uuid, TreeMap<String, String> params, String oauthConsumerSecret, String accessTokenSecret) throws OASException {
		String baseSignatureString = OAuthSignatureUtil.computeBaseSignatureString(method, uuid, params);
		logger.debug("Base Signature String = {}", baseSignatureString);
		System.out.println("BaseSign: " + baseSignatureString);
		System.out.println("Uuid: " + uuid);
		String oauthSignatureMethod = params.get(IOAuthSignatureVerifier.OAUTH_PARAMETER_oauth_signature_method);
		//String signature = computeSignature(baseSignatureString, oauthSignatureMethod, oauthConsumerSecret, accessTokenSecret, (url.getProtocol().equals(IProtocol.HTTPS)));
		String signature = computeSignature(baseSignatureString, oauthSignatureMethod, oauthConsumerSecret, accessTokenSecret, true);
		return signature;
	}

	private static String computeSignature(String baseSignatureString, String oauthSignatureMethod, String oauthConsumerSecret, String accessTokenSecret, boolean secureTransport) throws OASException {
		logger.trace("Signing {}\nwith method = {}\nck = {}\nat = {}", baseSignatureString, oauthSignatureMethod, oauthConsumerSecret, accessTokenSecret);
		if(oauthSignatureMethod.equals(IOAuthSignatureVerifier.OAUTH_SIGNATURE_METHOD_HMAC_SHA1)){
			String hmacKey = oauthConsumerSecret + "&" + accessTokenSecret;
			logger.debug("HMAC key = {}", hmacKey);
			byte[] signature = HMACSHA1.hmacSha1(baseSignatureString, hmacKey);
			logger.debug("got binary signature");
			String base64EncodedSignature = Base64.encode(signature);
			logger.debug("base64EncodedSignature = {}", base64EncodedSignature);
			String URLEncodedSignature;
			try{
				URLEncodedSignature = URLEncoder.encode(base64EncodedSignature, "UTF-8");
				logger.debug("URLEncodedSignature = {}", URLEncodedSignature);
			} catch (UnsupportedEncodingException e){
				logger.error(e.getMessage());
				throw new OASException(OASException.SERVER_ERROR, "There was an error while encoding signature");
			}
			logger.debug("Computed HMAC-SHA1 = {}", URLEncodedSignature);
			return URLEncodedSignature;
		}
		else if(oauthSignatureMethod.equals(IOAuthSignatureVerifier.OAUTH_SIGNATURE_METHOD_RSA_SHA1)){
			return null;
		}
		else if(oauthSignatureMethod.equals(IOAuthSignatureVerifier.OAUTH_SIGNATURE_METHOD_PLAINTEXT)){
			if(secureTransport){
				String signature = oauthConsumerSecret + "&" + accessTokenSecret;
				return signature;
			}
			else{
				throw new OASException(OASException.CLIENT_ERROR, "PLAINTEXT signature must run with TLS transport");
			}
		}
		return null;
	}

	private static String computeBaseSignatureString(String method, String uuid, TreeMap<String, String> params) throws OASException {
		method = method.toUpperCase();
		String normalizedURL = uuid;
		/*try{
			normalizedURL = OAuthSignatureUtil.getNormalizedEncodedURL(url);
		} catch (UnsupportedEncodingException e){
			logger.error(e.getMessage());
			throw new OASException(OASException.SERVER_ERROR, "There was an error while encoding URL: " + url.toString());
		}*/
		String normalizedParameters = null;
		try{
			normalizedParameters = OAuthSignatureUtil.getNormalizedParameters(params);
		} catch (UnsupportedEncodingException e){
			logger.error(e.getMessage());
			throw new OASException(OASException.SERVER_ERROR, "There was an error while encoding parameters");
		}
		String baseSignatureString = OAuthSignatureUtil.getBaseSignatureString(method, normalizedURL, normalizedParameters);
		logger.debug("Base Signature String = {}", baseSignatureString);
		return baseSignatureString;
	}

	public static TreeMap<String, String> getParameters(String uuid, String authorizationHeader) throws OASException {
		TreeMap<String, String> params = new TreeMap<String, String>();
		/*	UUID contains no parameter
		 * String query = url.getQuery();
		if(query != null){
			logger.debug("Query = {}", query);
			String[] queryParameters = query.split("&");
			for(int i = 0; i < queryParameters.length; i++){
				String parameter = queryParameters[i].trim();
				String[] parts = parameter.split("=");
				if(parts.length != 2) throw new OASException(OASException.SERVER_ERROR, "There was an error while parsing query at: " + parameter);
				params.put(parts[0].trim(), parts[1].trim());
			}
		}*/
		authorizationHeader = authorizationHeader.substring(IOAuthSignatureVerifier.OAUTH_AUTHORIZATION_HEADER_AUTH_SCHEME.length()).trim();
		String[] parameters = authorizationHeader.split(",\\s");
		for(int i = 0; i < parameters.length; i++){
			String parameter = parameters[i].trim();
			logger.debug("{} {}", new java.util.Date(), parameter);
			String[] parts = parameter.split("=");
			if(parts.length != 2) throw new OASException(OASException.SERVER_ERROR, "There was an error while parsing Authorization header at: " + parameter);
			if(parts[1].startsWith("\"") && parts[1].endsWith("\"")) parts[1] = parts[1].substring(1, parts[1].length() - 1);
			params.put(parts[0].trim(), parts[1].trim());
		}
		return params;
	}

	public static String getNormalizedURL(URL url) {
		String schema = url.getProtocol();
		String host = url.getHost();
		int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
		String path = url.getPath();
		StringBuilder sb = new StringBuilder();
		sb.append(schema);
		sb.append("://");
		sb.append(host);
		if(port != IOAuthSignatureVerifier.DEFAULT_HTTP_PORT && port != IOAuthSignatureVerifier.DEFAULT_HTTPS_PORT){
			sb.append(":" + port);
		}
		sb.append(path);
		String _url = new String(sb);
		return _url;
	}

	private static String getNormalizedEncodedURL(URL url) throws UnsupportedEncodingException {
		String _url = getNormalizedURL(url);
		String normalizedURL = URLEncoder.encode(_url, "UTF-8");
		return normalizedURL;
	}

	private static String getNormalizedParameters(TreeMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> parts = new ArrayList<String>();
		for(String key : params.keySet()){
			if(!key.equals(IOAuthSignatureVerifier.OAUTH_PARAMETER_oauth_signature)){
				parts.add(key + "=" + params.get(key));
			}
		}
		Iterator<String> it = parts.iterator();
		while (it.hasNext()){
			sb.append(it.next());
			if(it.hasNext()) sb.append("&");
		}
		String paramString = new String(sb);
		logger.debug("{}", paramString);
		String normalizedParamString = URLEncoder.encode(paramString, "UTF-8");
		logger.debug("{}", normalizedParamString);
		return normalizedParamString;
	}

	private static String getBaseSignatureString(String method, String normalizedURL, String normalizedParamString) {
		return method + "&" + normalizedURL + "&" + normalizedParamString;
	}

	public static String generateAuthorizationHeader(TreeMap<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append(IOAuthSignatureVerifier.OAUTH_AUTHORIZATION_HEADER_AUTH_SCHEME + " ");
		for(String key : params.keySet()){
			sb.append(key + "=" + "\"" + params.get(key) + "\"");
			if(key.equals(params.lastKey()) == false) sb.append(", ");
		}
		String authorizationHeader = new String(sb);
		return authorizationHeader;
	}
	
}
