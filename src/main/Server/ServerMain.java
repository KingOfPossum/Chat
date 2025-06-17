package main.Server;

public class ServerMain {
    private static final int port = 12345;

    public static void main(String[] args) {
        ChatServer server = new ChatServer(port);

        server.setMessageListener((sender,msg) -> {
            System.out.println(msg.message());
            if(msg.message().equals("Init")) {
                server.setClientUsername(sender,msg.userName());
            }
            else {
                System.out.println(sender + " : " + msg.message());
                server.broadcast(sender,msg);
            }
        });

        server.start();
    }
}
