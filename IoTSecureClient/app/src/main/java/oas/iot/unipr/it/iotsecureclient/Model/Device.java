package oas.iot.unipr.it.iotsecureclient.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicom on 18/06/15.
 */
public class Device {

    List<Resource> resources = new ArrayList<>();

    public List<Resource> getResources(){return resources;}

    public void setResources(List<Resource> res){this.resources = res;}

    private String uuid;
    public String getUuid(){return uuid;}
    public void setUuid(String uuid){this.uuid=uuid;}

    private String actions = "";
    public String getActions(){return actions;}
    public void setActions(String actions){this.actions = actions;}

    private String producer;
    public String getProducer(){return producer;}
    public void setProducer(String producer){this.producer=producer;}

    private String type;
    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    private String name;
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    private String model;
    public String getModel(){return model;}
    public void setModel(String model){this.model = model;}

    private String hostUrl;
    public String getHostUrl(){return hostUrl;}
    public void setHostUrl(String url){this.hostUrl = url;}

    public boolean hasAction(String action){return actions.contains(action);}

}
