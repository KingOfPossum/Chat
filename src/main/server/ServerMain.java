package main.server;

import main.common.connections.ServerConnectionListener;
import main.common.messages.MessageHistoryHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {
    private static final int PORT = 12345;

    private static MessageHistoryHandler messageHistoryHandler;;

    public static void main(String[] args) {
        Path messageHistoryPath = Paths.get("src","data","messageHistory.json");
        messageHistoryHandler = new MessageHistoryHandler(messageHistoryPath);

        ChatServer server = new ChatServer(PORT);

        server.setMessageListener((sender,msg) -> {
            if(msg.message().equals("Init")) {
                server.setClientUsername(sender,msg.userName());
            }
            else {
                System.out.println(sender + " : " + msg.message() + "(" + msg.timestamp() + ")");
                messageHistoryHandler.addMessage(msg);
                server.broadcast(sender,msg);
            }
        });

        server.setConnectionListener(new ServerConnectionListener(server,messageHistoryHandler.getMessageHistory()));

        server.start();
    }
}
