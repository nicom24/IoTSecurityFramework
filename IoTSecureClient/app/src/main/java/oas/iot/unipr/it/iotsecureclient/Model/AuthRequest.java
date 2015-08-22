package oas.iot.unipr.it.iotsecureclient.Model;

/**
 * Pojo for auth request
 * Created by nico on 30/04/15.
 */
public class AuthRequest {

    public AuthRequest(){}

    private String token;
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}

    private Device device;
    public Device getdevice(){return device;}
    public void setDevice(Device device){this.device=device;}

    private String actions;
    public String getActions(){return actions;}
    public void setActions(String actions){this.actions = actions;}

}
