package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        int port = 8788;
        ChatServer server = new ChatServer();

        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);

            while (true) {
                Socket socket = ss.accept();
                new Thread(new ClientHandler(socket, server)).start();
            }
        }
    }
}
