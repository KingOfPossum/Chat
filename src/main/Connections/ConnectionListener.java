package main.Connections;

public interface ConnectionListener {
    void onConnected();
    void onDisconnected();
    void onConnectionStatusChanged(ConnectionStatus previousStatus,ConnectionStatus currentStatus);
}
