package com.motorolasolutions.msismartreply;

import java.net.InetAddress;

public interface TheListener {

    void onClientConnected(InetAddress hostAddress);

    void onOwnerConnected();

    void onConnectionEstablished();

    void onConnectionLost();

    void onMessageReceived(String message);

}
