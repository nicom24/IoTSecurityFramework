package oas.iot.unipr.it.iotsecureclient.Model;

import java.util.Date;

/**
 * Pojo class for token
 * Created by nico on 30/04/15.
 */
public class Token {

    public Token(){}

    private String token;
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}

    private String secret;
    public String getSecret(){return secret;}
    public void setSecret(String secret){this.secret = secret;}

    private String type;
    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    private Date expires;
    public Date getExpires(){return expires;}
    public void setExpires(Date expires){this.expires = expires;}

}