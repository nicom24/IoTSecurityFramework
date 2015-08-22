package it.unipr.iot.oas;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.unipr.iot.oas.framework.IoTOASRequest;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * 
 * @author Nico Montali <a href="mailto:nico.montali@studenti.unipr.it">nico.montali@studenti.unipr.it</a>
 *
 */

public abstract class AbstractHttpHandler extends AbstractHandler{

	private static final int HTTP_OK = 200;
	private static final int HTTP_BAD_REQUEST = 400;
	private static final int HTTP_UNAUTH = 401;
	private static final int HTTP_SERVER_ERROR = 500;
	
	private IOAuthSignatureVerifier verifier;
	
	public void setVerifier(IOAuthSignatureVerifier verifier) {
		this.verifier = verifier;
	}
	
	protected abstract IoTOASRequest getIoTRequest(HttpServletRequest request)
			throws BadRequestException;
	
	/**
	 * Exception for a BAD_REQUEST code
	 * @author nico
	 *
	 */
	protected class BadRequestException extends Exception{
		
		public BadRequestException(){}
		
		public BadRequestException(String message){super(message);}
		
	}
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//Extract IoTOASRequest from request
		System.out.println("Received request");
		try{
			IoTOASRequest oasRequest = getIoTRequest(request);
			//Verify request from OAS service
			boolean valid = this.verifier.verify(oasRequest.getMethod(), oasRequest.getUuid(), oasRequest.getAuthorizationHeader());
			//Create response
			if (valid){
				baseRequest.setHandled(true);
				response.setStatus(HTTP_OK);
				response.getWriter().println("User is authorized");
			}else{
				baseRequest.setHandled(true);
				response.setStatus(HTTP_UNAUTH);
				response.getWriter().println("User is unauth");
			}
		}catch(BadRequestException e){
			baseRequest.setHandled(true);
			response.setStatus(HTTP_BAD_REQUEST);
			response.getWriter().println("Bad request");
		}catch(OASException e){
			e.printStackTrace();
			baseRequest.setHandled(true);
			response.setStatus(HTTP_SERVER_ERROR);
		}
	}
	
}
