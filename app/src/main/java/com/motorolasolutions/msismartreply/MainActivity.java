package com.motorolasolutions.msismartreply;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TheListener {
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 123; // Arbitrary request code
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES,};
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    WiFiDirectBroadcastReceiver receiver;

    private EditText messageInput;
    private ImageView sendMessage;

    private SmartReplyClient client;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new SmartReplyClient(getApplicationContext());
        handler = new Handler(Looper.getMainLooper());

        messageInput = findViewById(R.id.inputBar);
        sendMessage = findViewById(R.id.btn_send);

        sendMessage.setOnClickListener((View v) -> send(messageInput.getText().toString()));

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }

        // Indicates a change in the Wi-Fi Direct status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi Direct connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    private boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "discoverPeers onSuccess:");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "discoverPeers  onFailure");
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stopListening(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: stop listening");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "onFailure: stop listening " + reason);
            }
        });

        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, " removeGroup onSuccess: ");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, " removeGroup onFailure: "+reason);
            }
        });
    }


    @Override
    public void onClientConnected(InetAddress hostAddress) {

    }

    @Override
    public void onOwnerConnected() {

    }

    @Override
    public void onConnectionEstablished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onConnectionLost() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        handler.post(
                () -> {
                    client.loadModel();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        handler.post(
                () -> {
                    client.unloadModel();
                });
    }

    private void send(final String message) {
        Log.d(TAG, "send: ");
        handler.post(
                () -> {
                    SmartReply[] response = client.predict(new String[] {message});
                    Log.d(TAG, "send: size " + response.length);
                    ArrayList<String> replies = new ArrayList<String>();
                    for (SmartReply reply : response) {
                        replies.add(reply.getText());
                        Log.d(TAG, "send: reply is " + reply);
                    }
                });
    }
}