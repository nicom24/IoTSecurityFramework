package it.unipr.iot.oas.proxy;

import java.util.List;

import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.tools.resources.LinkAttribute;
import org.eclipse.californium.tools.resources.RDNodeResource;
import org.eclipse.californium.tools.resources.RDResource;

public class OASRDResource extends RDResource {

	public OASRDResource() {
		this("rd");
	}

	public OASRDResource(String resourceIdentifier) {
		super(resourceIdentifier);
	}
	
	/*
	 * POSTs a new sub-resource to this resource. The name of the new
	 * sub-resource is a random number if not specified in the Option-query.
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		
		// get name and lifetime from option query
		LinkAttribute attr;
		String endpointIdentifier = "";
		String domain = "local";
		RDNodeResource resource = null;
		
		ResponseCode responseCode;

		LOGGER.info("Registration request: "+exchange.getSourceAddress());
		
		List<String> query = exchange.getRequestOptions().getUriQuery();
		for (String q:query) {
			// FIXME Do not use Link attributes for URI template variables
			attr = LinkAttribute.parse(q);
			
			if (attr.getName().equals(LinkFormat.END_POINT)) {
				endpointIdentifier = attr.getValue();
			}
			
			if (attr.getName().equals(LinkFormat.DOMAIN)) {
				domain = attr.getValue();
			}
		}

		if (endpointIdentifier.equals("")) {
			exchange.respond(ResponseCode.BAD_REQUEST, "Missing endpoint (?ep)");
			LOGGER.info("Missing endpoint: "+exchange.getSourceAddress());
			return;
		}
		
		for (Resource node : getChildren()) {
			if (((RDNodeResource) node).getEndpointIdentifier().equals(endpointIdentifier) && ((RDNodeResource) node).getDomain().equals(domain)) {
				resource = (RDNodeResource) node;
			}
		}
		
		if (resource==null) {
			
			String randomName;
			do {
				randomName = Integer.toString((int) (Math.random() * 10000));
			} while (getChild(randomName) != null);
			
			resource = new RDNodeResource(endpointIdentifier, domain,"");
			add(resource);
			
			responseCode = ResponseCode.CREATED;
		} else {
			responseCode = ResponseCode.CHANGED;
		}
		
		// set parameters of resource
		if (!resource.setParameters(exchange.advanced().getRequest())) {
			resource.delete();
			exchange.respond(ResponseCode.BAD_REQUEST);
			return;
		}
		
		LOGGER.info("Adding new endpoint: "+resource.getContext());

		// inform client about the location of the new resource
		exchange.setLocationPath(resource.getURI());

		// complete the request
		exchange.respond(responseCode);
		
		
		//Update uuid translator
		/*while (resource.getChildren().iterator().hasNext()){
			Resource rChild = resource.getChildren().iterator().next();
			if (rChild.getAttributes().containsAttribute("uuid")){
				String uuid = rChild.getAttributes().getAttributeValues("uuid").get(0);
				String url = exchange.getSourceAddress().getHostAddress();
				System.out.println("Adding new resource @ " + url + " with uuid: " + uuid);
				return;
			}
		}*/
	}
	
}
