package oas.iot.unipr.it.iotsecureclient.Model;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by nico on 04/05/15.
 */
public class HMACSHAUtil {

    public static String hmacSha1(String value, String key){
        try {
            String type = "HmacSHA1";
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] bytes = mac.doFinal(value.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT).trim();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}

