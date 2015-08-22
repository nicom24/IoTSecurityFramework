package sun.net.www.protocol.coap;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * 
 * Extension of URLStreamHandler to implement coap:// URL
 * @author Nico Montali <a href="mailto:nico.montali@studenti.unipr.it">nico.montali@studenti.unipr.it</a>
 * 
 */

public class Handler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u){
		return new CoapConnection(u);
	}
	
}