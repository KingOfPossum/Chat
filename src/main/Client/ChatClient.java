package main.Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.gson.Gson;
import main.Connections.ConnectionStatus;
import main.Connections.ConnectionStatusListener;

public class ChatClient {
    private final int port;
    private final String serverIP;

    private Socket socket;

    private Gson gson = new Gson();

    private BufferedWriter writer;
    private BufferedReader reader;

    private MessageListener messageListener;
    private ConnectionStatusListener connectionStatusListener;

    private final AtomicBoolean closedConnection = new AtomicBoolean(false);
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    private int maxConnectionAttempts = 10;

    public ChatClient(String serverIP,int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setConnectionStatusListener(ConnectionStatusListener connectionStatusListener) {
        this.connectionStatusListener = connectionStatusListener;
    }

    public void setMaxConnectionAttempts(int maxConnectionAttempts) {
        this.maxConnectionAttempts = maxConnectionAttempts;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    private void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
        if(connectionStatusListener != null) {
            this.connectionStatusListener.onConnectionStatusChanged(connectionStatus);
        }
    }

    public void start() {
        connect();

        if(connectionStatus.equals(ConnectionStatus.CONNECTED)) {
            startListening();
        }
    }

    public void sendMessage(ChatMessage message) {
        if(connectionStatus.equals(ConnectionStatus.RECONNECTING)) {
            return;
        }

        try {
            String jsonMsg = gson.toJson(message);

            writer.write(jsonMsg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error while sending message");
            reconnect();
        }
    }

    public void closeConnection(){
        //Make sure the connection hasn't already been closed
        if(closedConnection.getAndSet(true)) return;

        try {
            if(socket != null) socket.close();
            if(writer != null) writer.close();
            if(reader != null) reader.close();

            setConnectionStatus(ConnectionStatus.DISCONNECTED);

            System.out.println("Connection closed");
        } catch (IOException e) {
            System.out.println("Error while closing connection");
        }
    }

    public void reconnect() {
        synchronized (this) {
            closeConnection();

            setConnectionStatus(ConnectionStatus.RECONNECTING);

            closedConnection.set(false);
            connect();
            startListening();
        }
    }

    private void connect() {
        try {
            if(!connectToServer()) {
                return;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if(writer != null && reader != null) {
                setConnectionStatus(ConnectionStatus.CONNECTED);
            }
            else {
                System.out.println("Writer or reader is null - connection failed");
            }

        } catch(IOException e) {
            System.out.println("Error while getting Output or Input Stream of socket : " + e.getMessage());
        }
    }

    private boolean connectToServer() throws InterruptedException {
        int connectionAttempts = 0;

        while(connectionAttempts < maxConnectionAttempts) {
            try {
                socket = new Socket(serverIP, port);
                System.out.println("Connected to " + socket.getInetAddress().toString());
                return true;
            } catch(IOException e) {
                System.out.println("Could not connect to server will try again in 1 second ...");
                connectionAttempts++;
                Thread.sleep(1000);
            }
        }

        System.out.println("Creating connection failed due to max connection attempts reached!");
        return false;
    }

    private void startListening() {
        Thread listenThread = new Thread(this::listen);
        listenThread.setDaemon(true);
        listenThread.start();
    }

    private void listen() {
        try {
            String message;
            while((message = reader.readLine()) != null)
            {
                try {
                    ChatMessage chatMessage = gson.fromJson(message,ChatMessage.class);
                    if(messageListener != null) {
                        messageListener.onMessageReceived(null,chatMessage);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to parse message : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading message");
        } finally {
            reconnect();
        }
    }

}
