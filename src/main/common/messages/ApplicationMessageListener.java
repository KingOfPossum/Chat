package main.common.messages;

import com.google.gson.Gson;
import javafx.application.Platform;
import main.client.ClientApplication;
import main.common.connections.LoginStatus;

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
                if(clientApp.loginStatus.equals(LoginStatus.NOT_LOGGED_IN)) {
                    return;
                }

                String messageHistoryJson = message.message().substring("MessageHistory".length());

                Gson gson = new Gson();

                MessageHistory messageHistory = gson.fromJson(messageHistoryJson,MessageHistory.class);

                clientApp.setTextAreaText("");
                for(ChatMessage chatMessage : messageHistory.history()) {
                    clientApp.updateTextArea(chatMessage.message(), chatMessage.userName());
                }
            }
            else if(message.message().startsWith("ConnectedClients")) {
                if(clientApp.loginStatus.equals(LoginStatus.NOT_LOGGED_IN)) {
                    return;
                }

                String substring = message.message().substring("ConnectedClients".length());
                System.out.println(substring);

                String msg = substring.replaceAll("([\\[\\] ])","");
                String[] clients = msg.split(",");

                Platform.runLater(() -> clientApp.updateClientsField(clients));
            }
            else if(message.message().startsWith("Login response: ")) {
                if(!clientApp.loginStatus.equals(LoginStatus.NOT_LOGGED_IN)) {
                    System.out.println("Received login response while already logged in");
                    return;
                }

                String response = message.message().substring("Login response: ".length());
                if(response.equals("Login successful")) {
                    Platform.runLater(() -> clientApp.setLoginStatus(LoginStatus.LOGIN_SUCCESS));
                    System.out.println("Login successful");
                }
                else if(response.equals("Login failed")) {
                    Platform.runLater(() -> clientApp.setLoginStatus(LoginStatus.LOGIN_FAILED));
                    System.out.println("Login failed");
                }
            }
            else {
                System.out.println("Unknown server message: " + message.message());
            }
        }
        else {
            if(clientApp.loginStatus.equals(LoginStatus.NOT_LOGGED_IN)) {
                return;
            }

            Platform.runLater(() -> clientApp.updateTextArea(message.message(),message.userName()));
        }
    }
}
