package main.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    private final String serverIP = "0.0.0.0";
    private final int port = 12345;
    private Socket clientSocket;
    private ChatClient chatClient;

    public void start() {
        try {
            connectToServer();
        } catch(InterruptedException e) {
            System.out.println("Server interrupted");
        }

        try {
            this.chatClient = new ChatClient(clientSocket);
            chatClient.start();

            //receiveInput(chatClient);
        } catch (IOException e) {
            System.out.println("Starting client failed : " + e.getMessage());
        }

    }

    public void stop() {
        if(chatClient != null) {
            chatClient.closeConnection();
        }
    }

    private void connectToServer() throws InterruptedException {
        while(true) {
            try {
                clientSocket = new Socket(serverIP,port);
                System.out.println("Connected to server : " + clientSocket.getInetAddress().toString());
                break;
            } catch (IOException e){
                System.out.println("Could not connect to server. Retrying in 1 second ...");
                Thread.sleep(1000);
            }
        }
    }

    public void sendMessage(String message) {
        this.chatClient.sendMessage(message);
    }

    private void receiveInput(ChatClient chatClient){
        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                String message = scanner.nextLine();

                if(message.equals("quit")) {
                    chatClient.closeConnection();
                    return;
                }

                chatClient.sendMessage(message);
            }
        } catch (Exception e){
            System.out.println("Input error : " + e.getMessage());
        }
    }
}
