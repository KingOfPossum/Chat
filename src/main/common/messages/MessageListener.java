package main.common.messages;

import java.net.Socket;

@FunctionalInterface
public interface  MessageListener {
    void onMessageReceived(Socket sender, ChatMessage message);
}