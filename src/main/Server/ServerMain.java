package main.Server;

public class ServerMain {
    private static final int port = 12345;

    public static void main(String[] args) {
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
