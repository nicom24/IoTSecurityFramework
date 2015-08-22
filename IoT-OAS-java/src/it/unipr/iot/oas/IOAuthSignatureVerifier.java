package it.unipr.iot.oas;

import java.net.URL;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public interface IOAuthSignatureVerifier {

	public static final String OAUTH_VERSION_1 = "1.0";

	public static final int DEFAULT_HTTP_PORT = 80;
	public static final int DEFAULT_HTTPS_PORT = 443;

	public static final String OAUTH_AUTHORIZATION_HEADER_AUTH_SCHEME = "OAuth";

	public static final String OAUTH_PARAMETER_oauth_consumer_key = "oauth_consumer_key";
	public static final String OAUTH_PARAMETER_oauth_token = "oauth_token";
	public static final String OAUTH_PARAMETER_oauth_signature_method = "oauth_signature_method";
	public static final String OAUTH_PARAMETER_oauth_timestamp = "oauth_timestamp";
	public static final String OAUTH_PARAMETER_oauth_nonce = "oauth_nonce";
	public static final String OAUTH_PARAMETER_oauth_version = "oauth_version";
	public static final String OAUTH_PARAMETER_oauth_signature = "oauth_signature";

	public static final String OAUTH_SIGNATURE_METHOD_PLAINTEXT = "PLAINTEXT";
	public static final String OAUTH_SIGNATURE_METHOD_HMAC_SHA1 = "HMAC-SHA1";
	public static final String OAUTH_SIGNATURE_METHOD_RSA_SHA1 = "RSA-SHA1";

	public boolean verify(String requestMethod, String uuid, String authorizationHeader) throws OASException;

}
