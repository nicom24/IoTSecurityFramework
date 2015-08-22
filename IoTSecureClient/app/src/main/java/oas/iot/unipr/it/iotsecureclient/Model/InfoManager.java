package oas.iot.unipr.it.iotsecureclient.Model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by nico on 30/04/15.
 */
public class InfoManager {

    private static final String FETCH_URL = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/fetch/";
    private static final String AUTH_URL = "http://www.nicom.altervista.org/iot/iot-oas/index.php/oas/authorizeToken/";

    private static InfoManager ourInstance = new InfoManager();

    public static InfoManager getInstance() {
        return ourInstance;
    }

    private InfoManager() {
        myProxyProvider = new HardcodedProxyProvider();
        defaultProxy = myProxyProvider.getProxies().get(0);
        if (defaultProxy!=null && directory==null) directory = new ResourceDirectory(defaultProxy);
    }

    private ConsumerInfo myInfo;
    public ConsumerInfo getMyInfo(){return myInfo;}
    public void setMyInfo(ConsumerInfo myInfo){
        this.myInfo = myInfo;
    }

    public String getFetchUrl(){
        return FETCH_URL + myInfo.getConsumer().getKey() + "/" + myInfo.getToken().getToken();
    }

    public String getAuthUrl(){
        return AUTH_URL + myInfo.getConsumer().getKey() + "/" + myInfo.getToken().getToken();
    }

    private IProxyProvider myProxyProvider;
    public IProxyProvider getMyProxyProvider(){return myProxyProvider;}

    private Proxy defaultProxy;
    public Proxy getDefaultProxy(){return defaultProxy;}
    public void setDefaultProxy(Proxy p){
        this.defaultProxy = p;
        directory.changeProxy();
    }

    private boolean showingOwned = false;
    public void setShowingOwned(boolean value){this.showingOwned = value;}
    public boolean isShowingOwned(){return showingOwned;}
    public List<Device> getShowedResources(){
        if (showingOwned){
            return myInfo.getDevices();
        }else{
            return directory.getResources();
        }
    }

    private ResourceDirectory directory;
    public ResourceDirectory getDirectory(){return directory;}

}
