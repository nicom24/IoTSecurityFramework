package oas.iot.unipr.it.iotsecureclient.Remote;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oas.iot.unipr.it.iotsecureclient.Model.Device;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Model.Resource;

/**
 * Created by nicom on 18/06/15.
 */
public class ResourceDirectoryGetter extends AsyncTask<String, Void, List<Device>>
        implements IResourceGetter<List<Device>>{

    private String url;
    private IResourceReady<List<Device>> listener;

    @Override
    protected List<Device> doInBackground(String... params) {
        try {

            URL myUrl = new URL(this.url);
            Log.d("URL","Request to: " + myUrl.toString());
            String response = getJsonFromUri(myUrl);
            //Gson gson = new GsonBuilder().setDateFormat("yyyy-dd-mm HH:mm:ss").create();
            //<coap://localhost:57449/PeopleCounter>;ep="abcd",,<coap://localhost:57449/.well-known>,,<coap://localhost:57449/.well-known/core>;ep="abcd",,<coap://localhost:57449/Lock>;ep="abcd",
            if (response!=null)
                return getResources(response);
            else
                Log.d("CIAONE","NULLLLL");
                //return gson.fromJson(response,ConsumerInfo.class);
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Device> result){
        if (result!=null)
            InfoManager.getInstance().getDirectory().setResources(result);
        listener.objectReceived(result);
    }

    public void requestResourceFromUrl(String url, IResourceReady<List<Device>> listener){
        this.url = url;
        this.listener = listener;
        this.execute();
    }

    //Analyze resource directory output
    public List<Device> getResources(String response){
        List<Device> devices = new ArrayList<>();
        String[] substr = response.split(",,");
        for (String s : substr){
            //Discard resource with well-know, used in discovery
            if (!s.contains(".well-known")){
                Log.d("String",s);
                //Extract host path
                String host = s.substring(s.indexOf("//")+2);
                host = host.substring(0,host.indexOf("/"));
                Device d = deviceAlreadyInList(devices,host);
                if (d==null){
                    d = new Device();
                    d.setHostUrl(host);
                    String values[] = s.substring(s.indexOf(">")).split(";");
                    for (String v : values){
                        if (v.contains("Brand")) d.setProducer(v.substring(v.indexOf("=")+2,v.length()-1));
                        if (v.contains("uuid")) d.setUuid(v.substring(v.indexOf("=") + 2, v.length() - 1));
                        if (v.contains("Name")) d.setModel(v.substring(v.indexOf("=") + 2, v.length() - 1));
                        if (v.contains("Type")) d.setType(v.substring(v.indexOf("=")+2,v.length()-1));
                        if (v.contains("title")) d.setName(v.substring(v.indexOf("=")+2,v.length()-1));
                    }
                    devices.add(d);
                }
                //Extract resource name
                Resource r = new Resource();
                String resName = s.substring(s.indexOf("//")+2);
                resName = resName.substring(resName.indexOf("/")+1);
                resName = resName.substring(0,resName.indexOf(">"));
                r.setName(resName);
                d.getResources().add(r);
            }
        }
        for (Device d : devices){
            Log.d("Device","@" + d.getHostUrl() + " - Brand: " + d.getProducer() + " - Name: " + d.getModel() + " - Type: " + d.getType() + " - uuid: " + d.getUuid());
            for (Resource r : d.getResources()){
                Log.d("With res", r.getName());
            }
        }
        return devices;
    }

    private Device deviceAlreadyInList(List<Device> devices, String host){
        for(Device d : devices){
            if (d.getHostUrl().equals(host)) return d;
        }
        return null;
    }

    //Get string from a url, perform an HTTP GET
    protected String getJsonFromUri(URL url){
        HttpURLConnection connection;
        String response = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(1000);
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
