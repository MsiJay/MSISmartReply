package com.motorolasolutions.msismartreply;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WiFiDirectBroadcastReceiver";
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    TheListener theListener;
    public static final String CLIENT_NAME = "";
    private final WifiP2pManager.ConnectionInfoListener connectionInfoListener = info -> {
        // The group owner's IP address is available
        if (info.groupFormed && info.isGroupOwner) {
            Log.i(TAG, "onConnectionInfoAvailable: I am the group owner");
            theListener.onOwnerConnected();
            // Do something as the group owner
        } else if (info.groupFormed) {
            Log.i(TAG, "onConnectionInfoAvailable: I am a regular member");
            // Do something as a regular member
            theListener.onClientConnected(info.groupOwnerAddress);
        }
    };

    @SuppressLint("MissingPermission")
    private WifiP2pManager.PeerListListener peerListListener = peerList -> {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        for (WifiP2pDevice device : refreshedPeers) {
            if(device.deviceName.equals(CLIENT_NAME) ){
                Log.d(TAG, "state is : "+device.status);
            }

            if (device.deviceName.equals(CLIENT_NAME) && device.status == WifiP2pDevice.AVAILABLE) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "connect onSuccess");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.w(TAG, "connect onFailure: "+reason );
                    }
                });
            }
        }

    };


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       TheListener theListener) {
        this.manager = manager;
        this.channel = channel;
        this.theListener = theListener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wi-Fi Direct mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.i(TAG, "onReceive: wifi direct enabled");
            } else {
                Log.w(TAG, "onReceive: wifi direct disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP
                Log.d(TAG, "onReceive: ");
                manager.requestConnectionInfo(channel, connectionInfoListener);
            }


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
