
package oas.iot.unipr.it.iotsecureclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import oas.iot.unipr.it.iotsecureclient.Model.Device;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Model.Resource;
import oas.iot.unipr.it.iotsecureclient.Remote.IResourceGetter;
import oas.iot.unipr.it.iotsecureclient.Remote.ResourceRequestPerformer;


public class ClientActivity extends ActionBarActivity implements View.OnClickListener{

    private Device myDevice;
    private Resource selectedResource;
    private List<RadioButton> radios = new ArrayList<>();
    private ActionBar myActionBar;
    private boolean DTLSactive = false;
    private String coapPort = "5683";
    private String coapProtocol = "coap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        //Toolbar setup
        Toolbar toolbar = (Toolbar)findViewById(R.id.client_toolbar);
        setSupportActionBar(toolbar);
        myActionBar = getSupportActionBar();
        myActionBar.setHomeButtonEnabled(false);
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setDisplayShowCustomEnabled(true);
        myActionBar.setTitle("REST client");
        //Get the device selected
        int poseca = getIntent().getExtras().getInt("device");
        myDevice = InfoManager.getInstance().getDirectory().getResources().get(poseca);
        selectedResource = myDevice.getResources().get(0);
        //Set info labels
        ((TextView)findViewById(R.id.client_device_name)).setText("Name: " + myDevice.getName());
        ((TextView)findViewById(R.id.client_device_uuid)).setText("uuid: " + myDevice.getUuid());
        ((TextView)findViewById(R.id.client_device_model)).setText("Model: " +myDevice.getModel());
        ((TextView)findViewById(R.id.client_device_brand)).setText("Producer: " +myDevice.getProducer());
        ((TextView)findViewById(R.id.client_device_type)).setText("Type: " + myDevice.getType());
        //Add radio buttons for resource choice
        RadioGroup rg = (RadioGroup)findViewById(R.id.client_radio_group);
        for (Resource r : myDevice.getResources()) {
            RadioButton radio = new RadioButton(this);
            radio.setText(r.getName());
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelected();
                }
            });
            rg.addView(radio);
            radios.add(radio);
        }
        radios.get(0).toggle();
        //Button listeners
        findViewById(R.id.client_get_button).setOnClickListener(this);
        findViewById(R.id.client_post_button).setOnClickListener(this);
        findViewById(R.id.client_put_button).setOnClickListener(this);
        findViewById(R.id.client_delete_button).setOnClickListener(this);
        findViewById(R.id.client_secure_button).setOnClickListener(this);
        ((Button)findViewById(R.id.client_secure_button)).setTextColor(getResources().getColor(R.color.primary_text));
    }

    private void updateSelected(){
        for (int i=0;i<radios.size();i++){
            if (radios.get(i).isChecked()) selectedResource = myDevice.getResources().get(i);
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId()==R.id.client_get_button){
            doRequest("GET");
        }else if (v.getId()==R.id.client_post_button){
            doRequest("POST");
        }else if (v.getId()==R.id.client_secure_button){
            DTLSactive = !DTLSactive;
            updateSecure();
        }
    }

    private void doRequest(String method){
        //GET,POST,PUT,DELETE
        String auth = InfoManager.getInstance().getMyInfo().generateAuthHeader(myDevice.getUuid(),method);
        IResourceGetter<String> performer = new ResourceRequestPerformer(this,method,"",auth);
        String url = InfoManager.getInstance().getDefaultProxy().getCompleteUrl() + coapProtocol + "://" + myDevice.getHostUrl() + "/" + selectedResource.getName();
        Log.d("DetailActivity", "Perform " + method +" to " + url);
        Log.d("Auth",auth);
        performer.requestResourceFromUrl(url,new IResourceGetter.IResourceReady<String>() {
            @Override
            public void objectReceived(String result) {
                if (result != null) {
                    ((TextView)findViewById(R.id.client_response)).setText("Response: " + result);
                    Log.d("Res", result);
                    if (result.equals("401 - Unauthorized")){
                        new AlertDialog.Builder(ClientActivity.this)
                                .setTitle("Unauthorized")
                                .setMessage("You are not authorized to perform this action.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                } else {
                    Log.d("Res", "No result");
                    ((TextView)findViewById(R.id.client_response)).setText("No response. ");
                }
            }
        });
    }

    private void updateSecure(){
        if (DTLSactive){
            ((Button)findViewById(R.id.client_secure_button)).setTextColor(getResources().getColor(R.color.green_500));
            //coapPort = "5684";
            coapProtocol = "coaps";
        }else{
            ((Button)findViewById(R.id.client_secure_button)).setTextColor(getResources().getColor(R.color.primary_text));
            //coapPort = "5683";
            coapProtocol = "coap";
        }
    }

}
