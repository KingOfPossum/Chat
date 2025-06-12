package main.Client;

import main.Client.UI.ClientApplication;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    private volatile boolean listen = true;

    public ChatClient(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void start() {
        Thread listenThread = new Thread(this::listen);
        listenThread.start();
    }

    private void listen() {
        try {
            while (listen) {
                String message = reader.readLine();
                System.out.println(message);
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
