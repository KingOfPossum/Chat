package main.common.connections;

import javafx.application.Platform;
import main.client.ClientApplication;

import java.net.Socket;

public class ApplicationConnectionListener implements ConnectionListener {

    private final ClientApplication clientApp;

    public ApplicationConnectionListener(ClientApplication clientApp) {
        this.clientApp = clientApp;
    }

    @Override
    public void onConnect(Socket client) {
        Platform.runLater(clientApp::initClient);
    }

    @Override
    public void onReconnect() {
        System.out.println("Reconnected");
    }

    @Override
    public void onDisconnect() {
        System.out.println("Disconnected");
    }

    @Override
    public void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus) {
        Platform.runLater(() -> clientApp.updateConnectionStatus(currentStatus));
    }

    @Override
    public void onConnectionFailed() {
        System.out.println("Connection failed");
    }
}
