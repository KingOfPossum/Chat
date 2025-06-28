package main.client;

import com.google.gson.Gson;
import main.common.connections.ConnectionStatus;
import main.common.messages.ChatMessage;
import main.common.TimeUtils;
import main.common.messages.MessageHistory;

import java.util.Scanner;

public class ClientMain {
    private static final String SERVER_IP = "0.0.0.0";
    private static final int PORT = 12345;

    private static ChatClient client;

    private static Scanner scanner = new Scanner(System.in);
    private static String userName;

    public static void main(String[] args) {
        setUserName();

        client = new ChatClient(SERVER_IP, PORT);

        client.setMessageListener((sender,chatMessage) -> {
            if(chatMessage.userName().startsWith("Server")) {
                if(chatMessage.message().startsWith("MessageHistory")) {
                    String messageHistoryJson = chatMessage.message().substring("MessageHistory".length());

                    Gson gson = new Gson();

                    MessageHistory messageHistory = gson.fromJson(messageHistoryJson,MessageHistory.class);

                    for(ChatMessage historyMessage : messageHistory.history()) {
                        System.out.println(historyMessage.userName() + " : " + historyMessage.message());
                    }
                }
            }
            else {
                System.out.println(chatMessage.userName() + " : " + chatMessage.message());
            }
        });

        client.start();

        if(client.getConnectionStatus().equals(ConnectionStatus.CONNECTED)){
            ChatMessage initMessage = new ChatMessage(userName,"Init", TimeUtils.currentTimestamp());
            client.sendMessage(initMessage);

            receiveUserInput();
        }
    }

    private static void setUserName() {
        System.out.print("Please enter your user name: ");
        userName = scanner.nextLine();
    }

    private static void receiveUserInput(){
        try {
            String message;
            while((message = scanner.nextLine()) != null) {
                if(message.equals("quit")) {
                    client.closeConnection();
                    return;
                }

                ChatMessage chatMessage = new ChatMessage(userName,message,TimeUtils.currentTimestamp());

                client.sendMessage(chatMessage);
            }
        } catch (Exception e){
            System.out.println("Input error : " + e.getMessage());
        }
    }
}
