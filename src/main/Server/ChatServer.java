package main.Server;

import com.google.gson.Gson;
import main.Client.ChatMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private final int port;

    private final List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
    private final Map<Socket, BufferedReader> clientReaders = Collections.synchronizedMap(new HashMap<>());
    private final Map<Socket, BufferedWriter> clientWriters = Collections.synchronizedMap(new HashMap<>());

    private ServerSocket serverSocket;

    private final Gson gson = new Gson();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Starting server failed : " + e.getMessage());
        }

        // Checks for new connections
        try {
            while(true) {
                Socket newConnection = this.serverSocket.accept();
                addClient(newConnection);
            }
        } catch (IOException e) {
            System.out.println("Error while accepting new client connection");
        }
    }

    private void addClient(Socket client) {
        try {
            this.clients.add(client);
            this.clientReaders.put(client,new BufferedReader(new InputStreamReader(client.getInputStream())));
            this.clientWriters.put(client,new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));

            System.out.println("New connection from " + client.getInetAddress().toString());

            Thread clientListenerThread = new Thread(()->listen(client));
            clientListenerThread.setDaemon(true);
            clientListenerThread.start();
        } catch(IOException e) {
            System.out.println("Error while adding client connection");
        }
    }

    private void removeClient(Socket client) {
        try {
            this.clients.remove(client);

            this.clientWriters.get(client).close();
            this.clientWriters.remove(client);

            this.clientReaders.get(client).close();
            this.clientReaders.remove(client);
        } catch(IOException e) {
            System.out.println("Error while removing client connection");
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

                ChatMessage chatMessage = gson.fromJson(message,ChatMessage.class);

                System.out.println(chatMessage.userName() + " : " + chatMessage.message());

                broadcast(clientSocket,chatMessage);
            }
        } catch (IOException e) {
            System.out.println("Error reading from client!");
            closeConnection(clientSocket);
        }
    }

    private void broadcast(Socket sender, ChatMessage chatMessage) {
        Socket currentClient = sender;

        String jsonMsg = gson.toJson(chatMessage);

        try {
            for (Socket client : this.clients) {
                currentClient = client;

                if (client.equals(sender)) {
                    continue;
                }

                BufferedWriter clientWriter = this.clientWriters.get(client);
                clientWriter.write(jsonMsg);
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
            removeClient(client);

            if(!client.isClosed()) {
                client.close();
            }

            System.out.println("Closed connection to " + client.getInetAddress().toString());
        } catch (IOException e) {
            System.out.println("Error while closing connection");
        }
    }
}