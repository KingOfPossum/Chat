package main.Client;

import java.util.Scanner;

public class ClientMain {
    private static final String serverIP = "0.0.0.0";
    private static final int port = 12345;

    private static ChatClient client;

    public static void main(String[] args) {
        client = new ChatClient(serverIP,port);
        client.start();

        client.setMessageListener(message -> {
            System.out.println("Got message : " + message);
        });

        receiveUserInput();
    }

    private static void receiveUserInput(){
        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                String message = scanner.nextLine();

                if(message.equals("quit")) {
                    client.closeConnection();
                    return;
                }

                client.sendMessage(message);
            }
        } catch (Exception e){
            System.out.println("Input error : " + e.getMessage());
        }
    }
}
