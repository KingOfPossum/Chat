package main.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    private static final int port = 12345;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server started at port " + port + "\nReady to accept connections!");

            ChatServer server = new ChatServer(serverSocket);
            server.start();

        } catch (IOException e) {
            System.out.println("Error while starting server!");
        }
    }
}
