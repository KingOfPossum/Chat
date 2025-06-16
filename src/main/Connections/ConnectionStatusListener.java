package main.Connections;

@FunctionalInterface
public interface ConnectionStatusListener {
    void onConnectionStatusChanged(ConnectionStatus status);
}
