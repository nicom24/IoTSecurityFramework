package oas.iot.unipr.it.iotsecureclient.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import oas.iot.unipr.it.iotsecureclient.Remote.IResourceGetter;
import oas.iot.unipr.it.iotsecureclient.Remote.ResourceDirectoryGetter;

/**
 * Created by nicom on 18/06/15.
 */
public class ResourceDirectory {

    private String RD_URL = "";

    List<Device> devices = new ArrayList<>();

    public List<Device> getResources(){return devices;}
    public void setResources(List<Device> devices){this.devices = devices;}

    public ResourceDirectory(Proxy defaultProxy){
        //if (InfoManager.getInstance().getDefaultProxy()!=null)
        RD_URL = defaultProxy.getCompleteUrl() + "coap://localhost:5683/rd-lookup/res";
        //else
            //Log.d("PROBLEMA", "PROBKEA");
    }

    public void changeProxy(){
        RD_URL = InfoManager.getInstance().getDefaultProxy().getCompleteUrl()  + "coap://localhost:5683/rd-lookup/res";
    }

    public void findResources(IResourceGetter.IResourceReady<List<Device>> listener){
        devices.clear();
        //Request resources to proxy
        ResourceDirectoryGetter getter = new ResourceDirectoryGetter();
        getter.requestResourceFromUrl(RD_URL,listener);
    }

}
