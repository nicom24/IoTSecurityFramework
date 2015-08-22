package oas.iot.unipr.it.iotsecureclient.Remote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import oas.iot.unipr.it.iotsecureclient.Model.ConsumerInfo;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;

/**
 * Created by nico on 30/04/15.
 */
public class AuthPoster extends AsyncTask<String, Void, Object>
        implements IResourceGetter<ConsumerInfo>{

    private IResourceReady<ConsumerInfo> listener;
    private String url;
    private String content;

    @Override
    protected Object doInBackground(String... params) {
        try {
            URL myUrl = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Content-Length","" + Integer.toString(content.getBytes().length));
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(content);
            wr.flush();
            wr.close();
            if (connection.getResponseCode() == 200){
                return new Object();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        if (object==null){
            Log.d("AuthPoster","Null response");
            return;
        }
        ConsumerInfoGetter g = new ConsumerInfoGetter();
        g.requestResourceFromUrl(InfoManager.getInstance().getFetchUrl(),listener);
    }

    public void requestResourceFromUrl(String url, IResourceReady<ConsumerInfo> listener){
        this.listener = listener;
        this.url = url;
        this.execute();
    }

    public void setContent(String content){this.content = content;}

}
