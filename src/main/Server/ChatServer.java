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

                if(message == null) {
                    closeConnection(clientSocket);
                    return;
                }

                System.out.println(message);

                sendMessage(clientSocket,message);
            }
        } catch (IOException e) {
            System.out.println("Error reading from client!");
            closeConnection(clientSocket);
        }
    }

    private void sendMessage(Socket sender, String message) {
        Socket currentClient = sender;

        try {
            for (Socket client : this.clients) {
                currentClient = client;

                if (client.equals(sender)) {
                    continue;
                }

                BufferedWriter clientWriter = this.clientWriters.get(client);
                clientWriter.write(message);
                clientWriter.newLine();
                clientWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("Error while writing to client!");
            closeConnection(currentClient);
        }
    }

    private void closeConnection(Socket client) {
        try {
            if(!client.isClosed()) {
                client.close();
            }

            this.clients.remove(client);

            this.clientWriters.get(client).close();
            this.clientWriters.remove(client);

            this.clientReaders.get(client).close();
            this.clientReaders.remove(client);

            System.out.println("Closed connection to " + client.getInetAddress().toString());
        } catch (IOException e) {
            System.out.println("Error while closing connection");
        }
    }
}