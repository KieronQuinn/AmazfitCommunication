package com.kieronquinn.app.amazfitinternetexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.huami.watch.transport.DataBundle;
import com.huami.watch.transport.DataTransportResult;
import com.huami.watch.transport.TransportDataItem;
import com.kieronquinn.library.amazfitcommunication.Transporter;
import com.kieronquinn.library.amazfitcommunication.TransporterClassic;

public class CustomActionActivity extends Activity {

    private TransporterClassic transporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create the transporter **WARNING** The second parameter MUST be the same on both your watch and phone companion apps!
        //Please change the module name to something unique, but keep it the same for both apps!
        transporter = (TransporterClassic) Transporter.get(this, "example_module");
        //Add a channel listener to listen for ready event
        transporter.addChannelListener(new Transporter.ChannelListener() {
            @Override
            public void onChannelChanged(boolean ready) {
                //Transporter is ready if ready is true, send an action now. This will **NOT** work before the transporter is ready!
                //You can change the action to whatever you want, there's also an option for a data bundle to be added (see below)
                if(ready)transporter.send("hello_world!");
            }
        });
        transporter.addDataListener(new Transporter.DataListener() {
            @Override
            public void onDataReceived(TransportDataItem transportDataItem) {
                Log.d("TransporterExample", "Item received action: " + transportDataItem.getAction());
                if(transportDataItem.getAction().equals("hello_world")) {
                    DataBundle receivedData = transportDataItem.getData();
                    //Do whatever with your action & data. You can send data back in the same way using the same transporter
                }
            }
        });
        transporter.connectTransportService();
    }

    @Override
    public void onStop(){
        super.onStop();
        transporter.removeAllChannelListeners();
        transporter.removeAllDataListeners();
        transporter.disconnectTransportService();
    }

    private void sendActionWithData(){
        //Create a bundle of data
        DataBundle dataBundle = new DataBundle();
        //Key value pair
        dataBundle.putString("hello", "world");
        //Send action
        transporter.send("hello_world_data", dataBundle);
    }

    private void sendActionWithDataAndCallback(){
        //Create a bundle of data
        DataBundle dataBundle = new DataBundle();
        //Key value pair
        dataBundle.putString("hello", "world");
        //Send action with a callback. This also works without the data bundle
        transporter.send("hello_world_data", dataBundle, new Transporter.DataSendResultCallback() {
            @Override
            public void onResultBack(DataTransportResult dataTransportResult) {
                Log.d("TransporterExample", "onResultBack result code " + dataTransportResult.getResultCode());
            }
        });
    }
}
