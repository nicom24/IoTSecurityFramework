package it.unipr.iot.oas.framework;

import it.unipr.iot.oas.AbstractHttpHandler;
import it.unipr.iot.oas.IOAuthSignatureVerifier;
import it.unipr.iot.oas.OASException;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 
 * 
 * @author Nico Montali <a href="mailto:nico.montali@studenti.unipr.it">nico.montali@studenti.unipr.it</a>
 * 
 */

public class IoTOASHTTPHandler extends AbstractHttpHandler{
	
	private static Logger logger = LoggerFactory.getLogger(IoTOASHTTPHandler.class);

	public IoTOASHTTPHandler(String protocol, IOAuthSignatureVerifier verifier) {
		//this.protocol = protocol;
		this.setVerifier(verifier);
	}
		
		/*
		 * (non-Javadoc) get IoTOASRequest from httpRequest, return null if invalid
		 * @see it.unipr.iot.oas.AbstractHttpHandler#getIoTRequest(javax.servlet.http.HttpServletRequest)
		 * 
		 */
	protected IoTOASRequest getIoTRequest(HttpServletRequest request) 
			throws BadRequestException{
		Gson gson = new Gson();
		//Check method and content
		String contentType = request.getContentType();
		if (!request.getMethod().equals("POST") || contentType==null || !contentType.equals(IoTOASRequest.HTTP_CONTENT_TYPE)){
			//Invalid request
			throw new BadRequestException();
		}else{
			//Read request body
			try{ 
				BufferedReader reader = request.getReader();
				//Get IoTRequest from json body
				IoTOASRequest oasRequest = gson.fromJson(reader, IoTOASRequest.class);
				return oasRequest;
			}catch(IOException e){
				throw new BadRequestException();
			}
		}	
	}
	
	
}
