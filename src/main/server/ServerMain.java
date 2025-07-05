package main.server;

import com.google.gson.Gson;
import main.common.TimeUtils;
import main.common.connections.ServerConnectionListener;
import main.common.messages.ChatMessage;
import main.common.messages.MessageHistoryHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {
    private static final int PORT = 12345;

    private static MessageHistoryHandler messageHistoryHandler;;

    public static void main(String[] args) {
        Path messageHistoryPath = Paths.get("src","data","messageHistory.json");
        messageHistoryHandler = new MessageHistoryHandler(messageHistoryPath);

        AccountManager accountManager = new AccountManager(Paths.get("src","data","accounts.json"));
        System.out.println(accountManager.accounts);

        ChatServer server = new ChatServer(PORT);

        server.setMessageListener((sender,msg) -> {
            if(msg.message().startsWith("Login : ")) {
                if(!accountManager.tryLogin(msg.userName(),msg.message().substring(8))) {
                    server.sendChatMessage(sender,new ChatMessage("Server","Login response: Login failed", TimeUtils.currentTimestamp()));
                    return;
                }

                server.getConnectionListener().onLogin(sender, msg.userName());
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
