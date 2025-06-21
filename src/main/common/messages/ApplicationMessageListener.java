package main.common.messages;

import javafx.application.Platform;
import main.client.ClientApplication;

import java.net.Socket;

public class ApplicationMessageListener implements MessageListener{

    ClientApplication clientApp;

    public ApplicationMessageListener(ClientApplication clientApp) {
        this.clientApp = clientApp;
    }

    @Override
    public void onMessageReceived(Socket sender, ChatMessage message) {
        if(message.userName() == null) {
            System.out.println(message.message());

            String msg = message.message().replaceAll("([\\[\\] ])","");
            String[] clients = msg.split(",");

            Platform.runLater(() -> clientApp.updateClientsField(clients));
        }
        else {
            Platform.runLater(() -> clientApp.updateTextArea(message.message(),message.userName()));
        }
    }
}
