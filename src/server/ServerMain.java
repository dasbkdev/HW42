package server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8788;
        new EchoServer(port).start();
    }
}
