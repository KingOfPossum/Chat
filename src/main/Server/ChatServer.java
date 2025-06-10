package main.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private final ServerSocket serverSocket;
    private BufferedReader reader;

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() throws IOException {
        // Checks for new connections
        Socket newConnection = this.serverSocket.accept();
        this.reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));

        System.out.println("New connection from " + newConnection.getInetAddress().toString());

        listen();
    }

    private void listen() throws IOException {
        while(true) {
            String message = reader.readLine();

            System.out.println(message);
        }
    }
}
