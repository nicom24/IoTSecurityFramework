package it.unipr.iot.oas.proxy;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.tools.ResourceDirectory;
import org.eclipse.californium.tools.resources.RDLookUpTopResource;
import org.eclipse.californium.tools.resources.RDTagTopResource;

public class OASResourceDirectory extends CoapServer {
    
    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    
    public static void main(String[] args) {
        
        // create server
        CoapServer server = new ResourceDirectory();
        server.start();
        
        System.out.printf(ResourceDirectory.class.getSimpleName()+" listening on port %d.\n", server.getEndpoints().get(0).getAddress().getPort());
    }
    
    public OASResourceDirectory() {
        
    	OASRDResource rdResource = new OASRDResource(); 

        // add resources to the server
		add(rdResource);
		add(new RDLookUpTopResource(rdResource));
		add(new RDTagTopResource(rdResource));
    }
}
