package it.unipr.iot.oas.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * 
 * @author Simone Cirani <a href="mailto:simone.cirani@unipr.it">simone.cirani@unipr.it</a>
 * 
 */

public class HMACSHA1 {

	public static byte[] hmacSha1(String value, String key) {
		try{
			// Get an hmac_sha1 key from the raw key bytes
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(value.getBytes());
			return rawHmac;
			/*
			 * // Convert raw bytes to Hex byte[] hexBytes = new Hex().encode(rawHmac); // Covert array of Hex bytes to a String return new String(hexBytes, "UTF-8");
			 */
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
