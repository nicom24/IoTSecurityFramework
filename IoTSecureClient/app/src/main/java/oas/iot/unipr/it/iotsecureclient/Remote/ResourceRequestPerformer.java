package oas.iot.unipr.it.iotsecureclient.Remote;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import oas.iot.unipr.it.iotsecureclient.R;

/**
 * Created by nico on 04/05/15.
 */
public class ResourceRequestPerformer extends AsyncTask<String, Void, String>
implements IResourceGetter<String> {

    private static final String TRUST_PWD = "rootPass";
    private static final String KEYSTORE_PWD = "endPass";


    private String url;
    private String content;
    private String method;
    private String authHeader;
    private Context context;
    private IResourceReady<String> listener;

    public ResourceRequestPerformer(Context context, String method, String content, String authHeader){
        this.method = method;
        this.content = content;
        this.context = context;
        this.authHeader = authHeader;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL myUrl = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            //connection.setSSLSocketFactory(getSSLFactory());
            connection.setRequestMethod(this.method);
            connection.setRequestProperty("Authorization",authHeader);
            connection.setDoInput(true);
            if (!this.method.equals("GET")) {
                //Send request
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(content.getBytes().length));
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(content);
                wr.flush();
                wr.close();
            }
            if (connection.getResponseCode() == 200){
                return "200-OK: " + getStringFromInputStream(connection.getInputStream());
            }else{
                return connection.getResponseCode() + " - " + connection.getResponseMessage();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String object) {
        listener.objectReceived(object);
    }

    public void requestResourceFromUrl(String url, IResourceReady<String> listener){
        this.url = url;
        this.listener = listener;
        this.execute();
    }

    protected String getStringFromInputStream(InputStream in){
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
            return sb.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private SSLSocketFactory getSSLFactory(){
        try {
            //Trust store
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream tis = context.getResources().openRawResource(R.raw.truststore);
            trustStore.load(tis,TRUST_PWD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] tMans = tmf.getTrustManagers();
            //Key store
            KeyStore keyStore = KeyStore.getInstance("BKS");
            InputStream kis = context.getResources().openRawResource(R.raw.keystore);
            keyStore.load(kis, KEYSTORE_PWD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, KEYSTORE_PWD.toCharArray());
            KeyManager[] kMans = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,tMans,null);
            return sslContext.getSocketFactory();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
