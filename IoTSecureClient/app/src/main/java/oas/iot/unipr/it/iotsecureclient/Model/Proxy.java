package oas.iot.unipr.it.iotsecureclient.Model;

/**
 * Created by nico on 04/05/15.
 */
public class Proxy {

    private static final String PROTOCOL = "http";
    private static final String PROXY_RESOURCE = "proxy";

    public Proxy(String name, String url){
        this.name = name;
        this.url = url;
    }

    private String name;
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    private String url;
    public String getUrl(){return url;}
    public void setUrl(String url){this.url = url;}

    public String getCompleteUrl(){
        return PROTOCOL + "://" + url + "/" + PROXY_RESOURCE + "/";
    }

}