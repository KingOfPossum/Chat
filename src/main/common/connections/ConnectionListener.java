package main.common.connections;

import java.net.Socket;

public interface ConnectionListener {
    void onConnect(Socket client);
    void onLogin(Socket client, String username);
    void onReconnect();
    void onDisconnect();
    void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus);
    void onConnectionFailed();
}
