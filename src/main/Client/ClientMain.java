package main.Client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    private static final String serverIP = "0.0.0.0";
    private static final int port = 12345;
    private static Socket clientSocket;

    public static void main(String[] args) throws IOException {
        try {
            connectToServer();
        } catch(InterruptedException e) {
            System.out.println("Server interrupted");
        }

        ChatClient client = new ChatClient(clientSocket);

        client.start();

        receiveInput(client);
    }

    private static void connectToServer() throws InterruptedException {
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

    private static void receiveInput(ChatClient chatClient){
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
