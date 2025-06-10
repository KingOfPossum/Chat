package main.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private final ServerSocket serverSocket;

    public ChatServer(ServerSocket serverSocket) {this.serverSocket = serverSocket;}

    public void start() throws IOException {
        // Checks for new connections
        Socket newConnection = this.serverSocket.accept();

        System.out.println("New connection from " + newConnection.getInetAddress().toString());
    }
}
