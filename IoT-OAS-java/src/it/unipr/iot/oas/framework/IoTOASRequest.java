package it.unipr.iot.oas.framework;

import java.net.URL;

/**
 * 
 * 
 * @author Nico Montali <a href="mailto:nico.montali@studenti.unipr.it">nico.montali@studenti.unipr.it</a>
 * 
 */

public class IoTOASRequest {

	public static final String HTTP_CONTENT_TYPE = "application/iotoas+json";
	
	private String method;
	public void setMethod(String method){this.method = method;}
	public String getMethod(){return method;}
	
	private String uuid;
	public void setUuid(String uuid){this.uuid = uuid;}
	public String getUuid(){return uuid;}
	
	private String authorizationHeader;
	public void setAuthorizationHeader(String authorizationHeader){this.authorizationHeader = authorizationHeader;}
	public String getAuthorizationHeader(){return authorizationHeader;}
	
}
