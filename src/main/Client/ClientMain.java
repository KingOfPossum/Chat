package main.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    private static final String ip = "0.0.0.0";
    private static final int port = 12345;

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName(ip);

        Socket clientSocket = new Socket(address,port);

        ChatClient client = new ChatClient(clientSocket);

        Thread clientThread = new Thread(client::start);
        clientThread.start();

        receiveInput(client);
    }

    private static void receiveInput(ChatClient chatClient) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String message = scanner.nextLine();

            if(message.equals("quit")) {
                chatClient.closeConnection();
            }

            chatClient.sendMessage(message);
        }
    }
}
