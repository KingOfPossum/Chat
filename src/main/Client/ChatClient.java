package main.Client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final Socket socket;
    private final BufferedWriter writer;

    public ChatClient(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void start() {
        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = reader.readLine();
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error while reading message");
        }
    }

    public void closeConnection() throws IOException {
        socket.close();
    }

    public void sendMessage(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}
