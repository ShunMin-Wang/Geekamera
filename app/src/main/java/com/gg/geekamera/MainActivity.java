package com.gg.geekamera;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "Geekamera";

    /* UI */
    private String[] list = {"No peer, press refresh to re-discover"};
    public ArrayAdapter<String> mAdapter;
    public ListView mPeerListView;
    public TextView mSelfIdTextView;


    /* WiFi P2P */
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    GeekBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPeerListView = (ListView) findViewById(R.id.listViewPeerList);
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        mPeerListView.setAdapter(mAdapter);
        mPeerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "WTF..you hit list", Toast.LENGTH_SHORT).show();
            }
        });

        mSelfIdTextView = (TextView)findViewById(R.id.textViewMyId);

        Button buttonDiscover = (Button) findViewById(R.id.buttonRefresh);
        if (buttonDiscover != null) {
            buttonDiscover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startDiscovering();
                }
            });
        }

        Button buttonVisible = (Button) findViewById(R.id.buttonBroadcast);
        if (buttonVisible != null) {
            buttonVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
        intentFilter =  new IntentFilter();
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new GeekBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void startDiscovering() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                Log.d(TAG, "discoverPeers success");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Log.d(TAG, "discoverPeers failed");
            }
        });
    }

    public void updatePeerList(List<String> clist) {
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, clist);
        mPeerListView.setAdapter(mAdapter);
    }

    public void updateSelfId(String id) {
        mSelfIdTextView.setText(getText(R.string.main_my_id) + "  " + id);
    }

}
