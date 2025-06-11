package main.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServer {
    private final ServerSocket serverSocket;

    private final List<Socket> clients = new ArrayList<>();
    private final HashMap<Socket,BufferedReader> clientReaders = new HashMap<>();
    private final HashMap<Socket, BufferedWriter> clientWriters = new HashMap<>();

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() {
        // Checks for new connections
        try {
            while(true) {
                Socket newConnection = this.serverSocket.accept();

                System.out.println("New connection from " + newConnection.getInetAddress().toString());

                this.clients.add(newConnection);
                this.clientReaders.put(newConnection,new BufferedReader(new InputStreamReader(newConnection.getInputStream())));
                this.clientWriters.put(newConnection,new BufferedWriter(new OutputStreamWriter(newConnection.getOutputStream())));

                Thread clientListenerThread = new Thread(()->listen(newConnection));
                clientListenerThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error while accepting new client connection");
        }
    }

    private void listen(Socket clientSocket) {
        try {
            BufferedReader clientReader = this.clientReaders.get(clientSocket);

            while(true) {
                String message = clientReader.readLine();

                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error reading from client!");
        }
    }
}
