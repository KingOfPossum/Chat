package main.Common.Messages;

import java.net.Socket;

@FunctionalInterface
public interface  MessageListener {
    void onMessageReceived(Socket sender, ChatMessage message);
}