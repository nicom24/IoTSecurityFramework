package oas.iot.unipr.it.iotsecureclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import oas.iot.unipr.it.iotsecureclient.Model.AuthRequest;
import oas.iot.unipr.it.iotsecureclient.Model.ConsumerInfo;
import oas.iot.unipr.it.iotsecureclient.Model.Device;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Remote.AuthPoster;
import oas.iot.unipr.it.iotsecureclient.Remote.IResourceGetter;


public class AuthorizationActivity extends ActionBarActivity implements IResourceGetter.IResourceReady<ConsumerInfo> {

    private static final int SCAN_REQUEST = 102;
    private Device myDevice;
    private ActionBar myActionBar;
    private AuthRequest myRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        //Toolbar setup
        Toolbar toolbar = (Toolbar)findViewById(R.id.auth_toolbar);
        setSupportActionBar(toolbar);
        myActionBar = getSupportActionBar();
        myActionBar.setHomeButtonEnabled(false);
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setDisplayShowCustomEnabled(true);
        myActionBar.setTitle("Manage authorizations");
        //Retrieve device
        int pos = getIntent().getExtras().getInt("device");
        myDevice = InfoManager.getInstance().getMyInfo().getDevices().get(pos);
        //Set info labels
        ((TextView)findViewById(R.id.auth_device_name)).setText("Name: " + myDevice.getName());
        ((TextView)findViewById(R.id.auth_device_uuid)).setText("uuid: " + myDevice.getUuid());
        ((TextView)findViewById(R.id.auth_device_brand)).setText("Producer: " + myDevice.getProducer());
        ((TextView)findViewById(R.id.auth_device_model)).setText("Model: " + myDevice.getModel());
        ((TextView)findViewById(R.id.auth_device_type)).setText("Type: " + myDevice.getType());
        //GET
        ((ImageView)findViewById(R.id.auth_get_icon)).setImageResource((myDevice.hasAction("GET")) ? R.drawable.ic_get_green : R.drawable.ic_get_grey);
        //POST
        ((ImageView)findViewById(R.id.auth_post_icon)).setImageResource((myDevice.hasAction("POST")) ? R.drawable.ic_post_green : R.drawable.ic_post_grey);
        //PUT
        ((ImageView)findViewById(R.id.auth_put_icon)).setImageResource((myDevice.hasAction("PUT")) ? R.drawable.ic_put_green : R.drawable.ic_put_grey);
        //DELETE
        ((ImageView)findViewById(R.id.auth_delete_icon)).setImageResource((myDevice.hasAction("DELETE")) ? R.drawable.ic_delete_red : R.drawable.ic_delete_grey);
        //AUTH
        ((ImageView)findViewById(R.id.auth_auth_icon)).setImageResource((myDevice.hasAction("AUTH")) ? R.drawable.ic_auth_yellow : R.drawable.ic_auth_grey);
        //OWN
        ((ImageView)findViewById(R.id.auth_own_icon)).setImageResource((myDevice.hasAction("OWN")) ? R.drawable.ic_own_yellow : R.drawable.ic_own_grey);
        ((TextView)findViewById(R.id.auth_own_text)).setText((myDevice.hasAction("OWN")) ? getString(R.string.owned_resource) : getString(R.string.unowned_resource));
        //Authorize button
        ((Button)findViewById(R.id.auth_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AUTH request
                myRequest = new AuthRequest();
                myRequest.setDevice(myDevice);
                createPermissionDialog(myDevice.getActions());
            }
        });
    }

    public void createPermissionDialog(String myPermissionsCopy){
        String myPermissions = myPermissionsCopy;
        if (myPermissions.contains("OWN")) myPermissions = myPermissions.replace(",OWN","");
        final String[] actions = myPermissions.split(",");
        final List<String> selectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set permission to give");
        builder.setMultiChoiceItems(actions, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            selectedItems.add(actions[indexSelected]);
                        } else if (selectedItems.contains(actions[indexSelected])) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user checked the checkbox
                            selectedItems.remove(actions[indexSelected]);
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                        StringBuilder builder = new StringBuilder();
                        for (String a : selectedItems) {
                            builder.append(a);
                            if (selectedItems.indexOf(a) != selectedItems.size()-1) builder.append(",");
                        }
                        myRequest.setActions(builder.toString());
                        launchScanner();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });
        AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
    }

    private void launchScanner(){
        try{
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE","QR_CODE_MODE");
            startActivityForResult(intent, SCAN_REQUEST);
        }catch(Exception e){
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SCAN_REQUEST && resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
            Log.d("Scanner", "Result: " + contents);
            //Qr code read succesfully, register uuid
            myRequest.setToken(contents);
            Gson gson = new Gson();
            AuthPoster poster = new AuthPoster();
            poster.setContent(gson.toJson(myRequest));
            poster.requestResourceFromUrl(InfoManager.getInstance().getAuthUrl(),this);
        }
    }

    @Override
    public void objectReceived(ConsumerInfo result) {

    }
}
