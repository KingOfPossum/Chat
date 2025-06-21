package main.common.connections;

public interface ConnectionListener {
    void onConnect();
    void onReconnect();
    void onDisconnect();
    void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus);
    void onConnectionFailed();
}
