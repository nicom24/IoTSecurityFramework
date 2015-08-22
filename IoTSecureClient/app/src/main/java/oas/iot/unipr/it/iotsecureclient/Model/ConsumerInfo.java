package oas.iot.unipr.it.iotsecureclient.Model;

import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Pojo class to handle the response of fetch
 * Created by nico on 30/04/15.
 */
public class
        ConsumerInfo {

    private static final String OAUTH_NONCE = "xyzxyz";
    private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String OAUTH_VERSION = "1.0";

    public ConsumerInfo(){resources = new ArrayList<>();}

    private Consumer consumer;
    public Consumer getConsumer(){return consumer;}
    public void setConsumer(Consumer consumer){this.consumer=consumer;}

    private Token token;
    public Token getToken(){return token;}
    public void setToken(Token token){this.token=token;}

    private List<Device> resources;
    public List<Device> getDevices(){return resources;}
    public void setDevices(List<Device> resources){this.resources = resources;}

    public Device getResourceWithUuid(String uuid){
        for (Device r : resources){
            if (r.getUuid().equals(uuid)) return r;
        }
        return null;
    }

    //EXAMPLE
    //OAuth oauth_consumer_key="vL-Y0YUbuG9_zViCwD98dJRZG", oauth_nonce="xyzxyz", oauth_signature="cu4jn2dVSfKDduVye%2BcIslnrwO0%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1369735200", oauth_token="Dxg5wMgH4VIIg6yoJasZWdN3m", oauth_version="1.0"
    public String generateAuthHeader(String uuid, String method){
        String params = "";
        try {
            params = URLEncoder.encode(getNormalizedParams("&", null, false), "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        String baseSignatureString = method + "&" + uuid + "&" + params;
        Log.d("base", baseSignatureString);
        String hmacKey = consumer.getSecret() + "&" + token.getSecret();
        String signature = HMACSHAUtil.hmacSha1(baseSignatureString,hmacKey);
        StringBuilder builder = new StringBuilder();
        builder.append("OAuth ");
        builder.append(getNormalizedParams(", ", signature, true));
        return builder.toString();
    }

    private String getNormalizedParams(String divider, String signature, boolean hasBrackets){
        String result = "";
        String brackets = (hasBrackets) ? "\"" : "";
        try {
            result += "oauth_consumer_key=" + brackets + consumer.getKey() + brackets + divider;
            result += "oauth_nonce=" + brackets + OAUTH_NONCE + brackets + divider;
            if (signature != null)
                result += "oauth_signature=" + brackets + URLEncoder.encode(signature,"UTF-8") + brackets + divider;
            result += "oauth_signature_method=" + brackets + OAUTH_SIGNATURE_METHOD + brackets + divider;
            result += "oauth_token=" + brackets + token.getToken() + brackets + divider;
            result += "oauth_version=" + brackets + OAUTH_VERSION + brackets;
            return result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }



}
