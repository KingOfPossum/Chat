package main.Common.Connections;

import javafx.application.Platform;
import main.Client.ClientApplication;

public class ApplicationConnectionListener implements ConnectionListener {

    private ClientApplication app;

    public ApplicationConnectionListener(ClientApplication application) {
        app = application;
    }

    @Override
    public void onConnect() {
        Platform.runLater(() -> app.initClient());
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
        Platform.runLater(() -> app.updateConnectionStatus(currentStatus));
    }

    @Override
    public void onConnectionFailed() {
        System.out.println("Connection failed");
    }
}
