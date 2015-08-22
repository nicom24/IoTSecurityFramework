package oas.iot.unipr.it.iotsecureclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import oas.iot.unipr.it.iotsecureclient.Model.AuthRequest;
import oas.iot.unipr.it.iotsecureclient.Model.Consumer;
import oas.iot.unipr.it.iotsecureclient.Model.ConsumerInfo;
import oas.iot.unipr.it.iotsecureclient.Model.Device;
import oas.iot.unipr.it.iotsecureclient.Model.IProxyProvider;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Model.Proxy;
import oas.iot.unipr.it.iotsecureclient.Model.Resource;
import oas.iot.unipr.it.iotsecureclient.Model.ResourceDirectory;
import oas.iot.unipr.it.iotsecureclient.Remote.AuthPoster;
import oas.iot.unipr.it.iotsecureclient.Remote.ConsumerInfoGetter;
import oas.iot.unipr.it.iotsecureclient.Remote.IResourceGetter;


public class MainActivity extends ActionBarActivity{

    private static final int QRCODE_INTENT = 101;

    private ActionBar myActionBar;
    private RecyclerView recycler;
    private RecyclerAdapter listAdapter;
    private LinearLayout noItemLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar setup
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        myActionBar = getSupportActionBar();
        myActionBar.setHomeButtonEnabled(false);
        myActionBar.setDisplayHomeAsUpEnabled(false);
        myActionBar.setDisplayShowCustomEnabled(true);
        myActionBar.setTitle(getResources().getString(R.string.main_title));
        //Drawer setup
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);
        findViewById(R.id.drawer_home_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Home
                InfoManager.getInstance().setShowingOwned(false);
                myActionBar.setTitle(getResources().getString(R.string.main_title));
                listAdapter.notifyDataSetChanged();
                mDrawerLayout.closeDrawers();
                findViewById(R.id.home_add_button).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.drawer_devices_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //My objects
                InfoManager.getInstance().setShowingOwned(true);
                myActionBar.setTitle(getResources().getString(R.string.my_device_title));
                listAdapter.notifyDataSetChanged();
                mDrawerLayout.closeDrawers();
                findViewById(R.id.home_add_button).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.drawer_share_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRDisplayActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });
        findViewById(R.id.drawer_logout_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logout
                Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toLogin);
                finish();
            }
        });
        //Recycler view setup
        recycler = (RecyclerView)findViewById(R.id.main_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        //Set adapter
        listAdapter = new RecyclerAdapter();
        recycler.setAdapter(listAdapter);
        noItemLayout = (LinearLayout)findViewById(R.id.no_item_layout);
        //Add button listener
        findViewById(R.id.home_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE","QR_CODE_MODE");
                    MainActivity.this.startActivityForResult(intent, QRCODE_INTENT);
                }catch(Exception e){
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }
            }
        });
        InfoManager.getInstance().getDirectory().findResources(new IResourceGetter.IResourceReady<List<Device>>() {
            @Override
            public void objectReceived(List<Device> result) {
                MainActivity.this.updateList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choose) {
            final IProxyProvider provider = InfoManager.getInstance().getMyProxyProvider();
            String[] proxiesAvailable = new String[provider.getProxies().size()];
            for (int i=0;i<provider.getProxies().size();i++){
                proxiesAvailable[i] = provider.getProxies().get(i).getName();
            }
            int selected = provider.getProxies().indexOf(InfoManager.getInstance().getDefaultProxy());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set network proxy");
            builder.setSingleChoiceItems(proxiesAvailable, selected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InfoManager.getInstance().setDefaultProxy(provider.getProxies().get(which));
                    Log.d("Proxy selected", InfoManager.getInstance().getDefaultProxy().getName());
                    InfoManager.getInstance().getDirectory().findResources(new IResourceGetter.IResourceReady<List<Device>>() {
                        @Override
                        public void objectReceived(List<Device> result) {
                            MainActivity.this.updateList();
                        }
                    });
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
            dialog.show();
            return true;
        } else if (id == R.id.action_refresh){
            //Refresh list, getting from the server the infos
            if (InfoManager.getInstance().isShowingOwned()){
                ConsumerInfoGetter g = new ConsumerInfoGetter();
                g.requestResourceFromUrl(InfoManager.getInstance().getFetchUrl(), new IResourceGetter.IResourceReady<ConsumerInfo>() {
                    @Override
                    public void objectReceived(ConsumerInfo result) {
                        updateList();
                    }
                });
            }else{
                InfoManager.getInstance().getDirectory().findResources(new IResourceGetter.IResourceReady<List<Device>>() {
                    @Override
                    public void objectReceived(List<Device> result) {
                        updateList();
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (InfoManager.getInstance().isShowingOwned())
            myActionBar.setTitle(getResources().getString(R.string.my_device_title));
        else
            myActionBar.setTitle(getResources().getString(R.string.main_title));
        updateList();
    }

    public void updateList(){
        //Update dataset
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == QRCODE_INTENT) {
            if (resultCode == RESULT_OK) {
                final Gson gson = new Gson();
                String contents = data.getStringExtra("SCAN_RESULT");
                contents = contents.replace("\\","");
                Log.d("Scanner", "Result: " + contents);
                //Qr code read succesfully, register device
                final Device readDevice = gson.fromJson(contents, Device.class);
                //Ask the user for a name
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose name:");
                final EditText editer = new EditText(this);
                builder.setView(editer);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        readDevice.setName(editer.getText().toString());
                        AuthRequest req = new AuthRequest();
                        req.setDevice(readDevice);
                        req.setToken(InfoManager.getInstance().getMyInfo().getToken().getToken());
                        req.setActions("");
                        AuthPoster poster = new AuthPoster();
                        poster.setContent(gson.toJson(req));
                        Log.d("Request", gson.toJson(req));
                        poster.requestResourceFromUrl(InfoManager.getInstance().getAuthUrl(), new IResourceGetter.IResourceReady<ConsumerInfo>() {
                            @Override
                            public void objectReceived(ConsumerInfo result) {
                                updateList();
                            }
                        });
                    }
                });
                builder.show();
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

}
