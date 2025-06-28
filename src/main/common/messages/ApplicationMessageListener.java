package main.common.messages;

import com.google.gson.Gson;
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
        if(message.userName().equals("Server")) {
            if(message.message().startsWith("MessageHistory")) {
                String messageHistoryJson = message.message().substring("MessageHistory".length());

                Gson gson = new Gson();

                MessageHistory messageHistory = gson.fromJson(messageHistoryJson,MessageHistory.class);

                clientApp.setTextAreaText("");
                for(ChatMessage chatMessage : messageHistory.history()) {
                    clientApp.updateTextArea(chatMessage.message(), chatMessage.userName());
                }
            }
            else if(message.message().startsWith("ConnectedClients")) {
                String substring = message.message().substring("ConnectedClients".length());
                System.out.println(substring);

                String msg = substring.replaceAll("([\\[\\] ])","");
                String[] clients = msg.split(",");

                Platform.runLater(() -> clientApp.updateClientsField(clients));
            }
        }
        else {
            Platform.runLater(() -> clientApp.updateTextArea(message.message(),message.userName()));
        }
    }
}
