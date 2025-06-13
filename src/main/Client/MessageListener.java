package main.Client;

@FunctionalInterface
public interface MessageListener {
    void onMessageReceived(ChatMessage message);
}
