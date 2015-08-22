package oas.iot.unipr.it.iotsecureclient.Remote;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import oas.iot.unipr.it.iotsecureclient.Model.ConsumerInfo;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;

/**
 * Created by nicom on 26/05/15.
 */
public class ConsumerInfoGetter extends AsyncTask<String, Void, ConsumerInfo>
        implements IResourceGetter<ConsumerInfo>{

    private String url;
    private IResourceReady<ConsumerInfo> listener;

    @Override
    protected ConsumerInfo doInBackground(String... params) {
        try {
            URL myUrl = new URL(this.url);
            String response = getJsonFromUri(myUrl);
            Log.d("consumer info",response);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-dd-mm HH:mm:ss").create();
            if (response!=null){
                return gson.fromJson(response,ConsumerInfo.class);
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ConsumerInfo result){
        InfoManager.getInstance().setMyInfo(result);
        listener.objectReceived(result);
    }

    public void requestResourceFromUrl(String url, IResourceReady<ConsumerInfo> listener){
        this.url = url;
        this.listener = listener;
        this.execute();
    }

    //Get JSON string from a url, perform an HTTP GET
    protected String getJsonFromUri(URL url){
        HttpURLConnection connection;
        String response = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            if (connection.getResponseCode() == 200){
                response = getStringFromInputStream(connection.getInputStream());
            }
            connection.disconnect();
        }catch(IOException e){
            e.printStackTrace();
        }
        return response;
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

}