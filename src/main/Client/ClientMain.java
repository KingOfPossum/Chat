package main.Client;

import main.ConnectionStatus;

import java.util.Scanner;

public class ClientMain {
    private static final String serverIP = "0.0.0.0";
    private static final int port = 12345;

    private static ChatClient client;

    private static Scanner scanner = new Scanner(System.in);
    private static String userName;

    public static void main(String[] args) {
        setUserName();

        client = new ChatClient(serverIP,port);
        client.start();

        if(client.getConnectionStatus().equals(ConnectionStatus.CONNECTED)){
            ChatMessage initMessage = new ChatMessage(userName,"Init");
            client.sendMessage(initMessage);

            client.setMessageListener((sender,chatMessage) -> {
                if(chatMessage.userName() == null) {
                    return;
                }
                System.out.println(chatMessage.userName() + " : " + chatMessage.message());
            });

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

                ChatMessage chatMessage = new ChatMessage(userName,message);

                client.sendMessage(chatMessage);
            }
        } catch (Exception e){
            System.out.println("Input error : " + e.getMessage());
        }
    }
}
