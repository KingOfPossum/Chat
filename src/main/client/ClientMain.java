package main.client;

import com.google.gson.Gson;
import main.common.connections.ConnectionStatus;
import main.common.messages.ChatMessage;
import main.common.TimeUtils;
import main.common.messages.MessageHistory;

import java.util.Scanner;

public class ClientMain {
    private static String serverIP = "0.0.0.0";
    private static int port = 12345;

    private static ChatClient client;

    private static Scanner scanner = new Scanner(System.in);
    private static String userName;

    public static void main(String[] args) {
        setIP();
        setPort();

        System.out.println("IP : " + serverIP + "\tPort : " + port);

        setUserName();

        client = new ChatClient(serverIP, port);

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

    private static void setIP(){
        System.out.print("IP (default: " + serverIP + "): ");
        String input = scanner.nextLine();
        if(!input.isEmpty()) {
            serverIP = input;
        }
    }

    private static void setPort() {
        System.out.print("Port (default: " + port + "): ");
        String input = scanner.nextLine();
        if(!input.isEmpty()) {
            port = Integer.parseInt(input);
        }
    }

    private static void setUserName() {
        do {
            System.out.print("Please enter your user name: ");
            userName = scanner.nextLine();

            if(userName.isEmpty()){
                System.out.println("Username cannot be empty. Please try again.");
            }
        } while(userName.isEmpty());
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
