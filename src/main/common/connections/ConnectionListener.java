package main.common.connections;

import java.net.Socket;

public interface ConnectionListener {
    void onConnect(Socket client);
    void onReconnect();
    void onDisconnect();
    void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus);
    void onConnectionFailed();
}
