package oas.iot.unipr.it.iotsecureclient.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 04/05/15.
 */
public class HardcodedProxyProvider implements IProxyProvider {

    private List<Proxy> proxies;

    public HardcodedProxyProvider(){
        proxies = new ArrayList<>();
        proxies.add(new Proxy("Guglielmo","172.19.0.106:9080"));
        proxies.add(new Proxy("Genymotion","192.168.56.1:9080"));
        proxies.add(new Proxy("0.7","192.168.0.7:9080"));

    }

    public List<Proxy> getProxies(){
        return proxies;
    }

}