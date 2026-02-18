package server;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String name;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.name = "User_" + UUID.randomUUID().toString().substring(0, 5);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Your name is: " + name);

            String message;
            while ((message = in.readLine()) != null) {
                server.broadcast(name + ": " + message, this);
            }

        } catch (IOException e) {
            System.out.println(name + " disconnected.");
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
