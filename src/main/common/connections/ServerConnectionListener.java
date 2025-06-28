package main.common.connections;

import main.server.ChatServer;
import main.server.ServerMain;

public class ServerConnectionListener implements ConnectionListener {

    private final ChatServer server;

    public ServerConnectionListener(ChatServer server) {
        this.server = server;
    }

    @Override
    public void onConnect() {}

    @Override
    public void onReconnect() {}

    @Override
    public void onDisconnect() {}

    @Override
    public void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus) {}

    @Override
    public void onConnectionFailed() {}
}
