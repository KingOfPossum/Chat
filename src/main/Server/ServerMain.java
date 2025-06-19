package main.Server;

public class ServerMain {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        ChatServer server = new ChatServer(PORT);

        server.setMessageListener((sender,msg) -> {
            if(msg.message().equals("Init")) {
                server.setClientUsername(sender,msg.userName());
            }
            else {
                System.out.println(sender + " : " + msg.message() + "(" + msg.timestamp() + ")");
                server.broadcast(sender,msg);
            }
        });

        server.start();
    }
}
