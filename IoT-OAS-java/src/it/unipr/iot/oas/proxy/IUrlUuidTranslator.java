package it.unipr.iot.oas.proxy;

import java.net.URL;

public interface IUrlUuidTranslator {

	public String UuidFromUrl(String url);
	
	public String UrlFromUuid(String uuid);
	
}
