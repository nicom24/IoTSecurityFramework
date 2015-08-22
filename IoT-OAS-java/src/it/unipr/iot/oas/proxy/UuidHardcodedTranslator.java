package it.unipr.iot.oas.proxy;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UuidHardcodedTranslator implements IUrlUuidTranslator{

	private static UuidHardcodedTranslator instance;
	
	private Map<String, String> urlMap;
	private Map<String, String> uuidMap;
	
	private String[] uuids = {
			"f8b1d5ad-932c-4fd0-ae09-b3d9e6c291c0",
			"87b9f26f-c8a0-4a35-b034-348e92061b31",
			"fefb0477-2636-4338-8358-16867ab023d1",
			"64e6b0e3-8480-4854-9eaa-90214ff9c362"
	};
	
	private String[] urls = {
			"coap://172.19.0.105:5684/",
			"coap://172.19.0.105:5683/",
			"coap://172.19.0.105:5688/",
			"coap://172.19.0.105:5689/"
	};
	
	private UuidHardcodedTranslator(){
		urlMap = new HashMap<>();
		uuidMap = new HashMap<>();
		for(int i=0; i<uuids.length; i++){
			urlMap.put(urls[i], uuids[i]);
			uuidMap.put(uuids[i], urls[i]);
		}
	}
	
	public static UuidHardcodedTranslator getInstance(){
		if (instance == null) instance = new UuidHardcodedTranslator();
		return instance;
	}
	
	public String UuidFromUrl(String url){
		return urlMap.get(removeResource(url));
	}
	
	public String UrlFromUuid(String uuid){
		return uuidMap.get(uuid);
	}
	
	private String removeResource(String uri){
		String protocol = uri.substring(0, uri.indexOf("://")+3);
		String host = uri.substring(uri.indexOf("://")+3);
		String hoster = host.substring(0, host.indexOf("/")+1);
		return protocol + hoster ;
	}
}
