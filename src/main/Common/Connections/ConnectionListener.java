package main.Common.Connections;

public interface ConnectionListener {
    void onConnected();
    void onDisconnected();
    void onConnectionStatusChanged(ConnectionStatus previousStatus,ConnectionStatus currentStatus);
}
