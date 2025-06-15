package main.Client;

import java.net.Socket;

@FunctionalInterface
public interface MessageListener {
    void onMessageReceived(Socket sender, ChatMessage message);
}