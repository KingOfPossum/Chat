package main.common.connections;

import com.google.gson.Gson;
import main.common.messages.ChatMessage;
import main.common.messages.MessageHistory;
import main.server.ChatServer;
import main.common.TimeUtils;

import java.net.Socket;

public class ServerConnectionListener implements ConnectionListener {

    private final ChatServer server;
    private MessageHistory messageHistory;

    public ServerConnectionListener(ChatServer server, MessageHistory messageHistory) {
        this.server = server;
        this.messageHistory = messageHistory;
    }

    @Override
    public void onConnect(Socket client) {
        System.out.println("Client connected: " + client.getInetAddress());
    }

    public void onLogin(Socket client, String username) {
        server.sendChatMessage(client,new ChatMessage("Server","Login response: Login successful", TimeUtils.currentTimestamp()));
        server.setClientUsername(client,username);

        Gson gson = new Gson();
        ChatMessage historyMessage = new ChatMessage("Server","MessageHistory" + gson.toJson(messageHistory), TimeUtils.currentTimestamp());
        server.sendChatMessage(client, historyMessage);
    }

    @Override
    public void onReconnect() {}

    @Override
    public void onDisconnect() {}

    @Override
    public void onConnectionStatusChange(ConnectionStatus previousStatus, ConnectionStatus currentStatus) {}

    @Override
    public void onConnectionFailed() {}
}
