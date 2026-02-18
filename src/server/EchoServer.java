package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server started on port " + port);

        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + clientSocket);

                pool.submit(() -> {
                    ClientConnection connection = new ClientConnection(clientSocket);
                    try {
                        connection.run();
                    } finally {
                        System.out.println("Client disconnected: " + clientSocket);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
