package sun.net.www.protocol.coap;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * Extension of URLConnection to implement coap:// URL
 * @author Nico Montali <a href="mailto:nico.montali@studenti.unipr.it">nico.montali@studenti.unipr.it</a>
 * 
 */

public class CoapConnection extends URLConnection {

	public CoapConnection(URL u){
		super(u);
	}
	
	@Override
	public void connect(){
		
	}
	
	@Override
	public Object getContent(){
		return null;
	}
	
	@Override
	public InputStream getInputStream(){
		return null;
	}
	
}
