package main.Server;

import com.google.gson.Gson;
import main.Common.Messages.ChatMessage;
import main.Common.Messages.MessageListener;
import main.Common.TimeUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private final int PORT;

    private final List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
    private final Map<Socket,String> clientToUsername = new HashMap<>();
    private final Map<Socket, BufferedReader> clientReaders = Collections.synchronizedMap(new HashMap<>());
    private final Map<Socket, BufferedWriter> clientWriters = Collections.synchronizedMap(new HashMap<>());

    private ServerSocket serverSocket;

    private final Gson gson = new Gson();
    private MessageListener messageListener;

    private final int loggingInterval = 5000;

    public ChatServer(int port) {
        this.PORT = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Starting server failed : " + e.getMessage());
        }

        Thread logClientsThread = new Thread(this::logClients);
        logClientsThread.start();

        waitForNewClients();
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setClientUsername(Socket client, String username) {
        this.clientToUsername.put(client,username);
    }

    public void broadcast(Socket sender, ChatMessage chatMessage) {
        Socket currentClient = sender;

        String jsonMsg = gson.toJson(chatMessage);

        try {
            for (Socket client : this.clients) {
                currentClient = client;

                if(sender != null){
                    if (client.equals(sender)) {
                        continue;
                    }
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

    private void logClients() {
        while (true) {
            System.out.println("Connected Clients : " + Arrays.toString(clientToUsername.values().toArray()));

            ChatMessage connectedClientsMsg = new ChatMessage(null,Arrays.toString(clientToUsername.values().toArray()), TimeUtils.currentTimestamp());
            broadcast(null,connectedClientsMsg);
            try {
                Thread.sleep(loggingInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void waitForNewClients() {
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

            this.clientToUsername.remove(client);
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
                messageListener.onMessageReceived(clientSocket,chatMessage);
            }
        } catch (IOException e) {
            System.out.println("Error reading from client!");
            closeConnection(clientSocket);
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