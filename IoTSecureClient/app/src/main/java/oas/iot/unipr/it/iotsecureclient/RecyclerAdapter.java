package oas.iot.unipr.it.iotsecureclient;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import oas.iot.unipr.it.iotsecureclient.Model.Device;
import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;
import oas.iot.unipr.it.iotsecureclient.Model.Resource;

/**
 * Created by nicom on 27/05/15.
 */
public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    //Constant TAG for Log
    private static final String TAG = "RecyclerAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        //Constant TAG for Log
        private static final String TAG = "ViewHolder";
        //Class properties
        private View vMain = null;
        private int position;
        private Device myDevice;

        public void setPosition(int position) {
            this.position = position;
        }

        //Class constructor
        public ViewHolder(View v) {
            super(v);
            this.vMain = v;
            //Set view listeners, onClickListener o onLongClickListener
            vMain.setOnClickListener(this);
        }

        public void setDevice(Device d) {
            myDevice = d;
            ((TextView)vMain.findViewById(R.id.element_text_name)).setText("Name: " + d.getName());
            ((TextView)vMain.findViewById(R.id.element_text_uuid)).setText("Uuid: " + d.getUuid());
            ((TextView)vMain.findViewById(R.id.element_text_producer)).setText("Producer: " + d.getProducer());
            ((TextView)vMain.findViewById(R.id.element_text_model)).setText("Model: " + d.getModel());
        }

        @Override
        public void onClick(View v) {
            //Start activity for a detailed view
            Intent intent;
            if (InfoManager.getInstance().isShowingOwned()) {
                intent = new Intent(vMain.getContext(), AuthorizationActivity.class);
                intent.putExtra("device",position);
            }else{
                intent = new Intent(vMain.getContext(), ClientActivity.class);
                intent.putExtra("device", position);
            }
            vMain.getContext().startActivity(intent);
        }

    }

    //Class constructor
    public RecyclerAdapter() {
    }

    //Create new views (invoked by layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //Replace the contents of a view (invoked by layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Device r = InfoManager.getInstance().getShowedResources().get(position);
        holder.setDevice(r);
        holder.setPosition(position);
    }

    //Return the size of the dataset
    @Override
    public int getItemCount() {
        return InfoManager.getInstance().getShowedResources().size();
    }


}