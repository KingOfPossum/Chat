package main.Client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final int port;
    private final String serverIP;

    private Socket socket;

    private BufferedWriter writer;
    private BufferedReader reader;

    private MessageListener messageListener;

    private volatile boolean listen = true;

    public ChatClient(String serverIP,int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void start() {
        // Try to connect to the server
        try {
            connectToServer();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // Set our writer and reader so we can send and receive data
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e) {
            System.out.println("Error while getting Output or Input Stream of socket : " + e.getMessage());
        }

        // Start listening for incoming data
        Thread listenThread = new Thread(this::listen);
        listenThread.start();
    }

    private void connectToServer() throws InterruptedException {
        while(true) {
            try {
                socket = new Socket(serverIP, port);
                System.out.println("Connected to " + socket.getInetAddress().toString());
                break;
            } catch(IOException e) {
                System.out.println("Could not connect to server will try again in 1 second ...");
                Thread.sleep(1000);
            }
        }
    }

    public void listen() {
        try {
            String message;
            while((message = reader.readLine()) != null)
            {
                if(messageListener != null) {
                    messageListener.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading message");
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error while sending message");
            closeConnection();
        }
    }

    public void closeConnection(){
        if(!listen) {return;}

        listen = false;

        try {
            socket.close();
            writer.close();
            reader.close();

            System.out.println("Connection closed");
        } catch (IOException e) {
            System.out.println("Error while closing connection");
        }
    }
}
