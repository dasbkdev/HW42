package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;

    private BufferedReader in;
    private PrintWriter out;

    private volatile String name;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.name = "User_" + UUID.randomUUID().toString().substring(0, 5);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            server.addClient(this);
            server.broadcast("System: " + name + " joined the chat", this);

            sendMessage("System: your name is " + name);
            sendMessage("System: commands: /name NEWNAME | /list | /w NAME TEXT | /bye");

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("/")) {
                    if (!handleCommand(line)) {
                        sendMessage("System: wrong command");
                    }
                    continue;
                }

                server.broadcast(name + ": " + line, this);
            }

        } catch (IOException e) {
        } finally {
            server.removeClient(this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private boolean handleCommand(String line) {
        if (line.equalsIgnoreCase("/list")) {
            sendMessage("System: users: " + server.listUsers());
            return true;
        }

        if (line.equalsIgnoreCase("/bye")) {
            sendMessage("bye");
            try { socket.close(); } catch (IOException ignored) {}
            return true;
        }

        if (line.toLowerCase().startsWith("/name ")) {
            String newName = line.substring(6).trim();
            boolean ok = server.rename(this, newName);
            if (ok) {
                sendMessage("System: name changed to " + name);
            } else {
                sendMessage("System: name is invalid or already taken");
            }
            return true;
        }

        if (line.toLowerCase().startsWith("/w ")) {
            String rest = line.substring(3).trim();
            int sp = rest.indexOf(' ');
            if (sp == -1) {
                sendMessage("System: usage /w NAME TEXT");
                return true;
            }
            String toName = rest.substring(0, sp).trim();
            String msg = rest.substring(sp + 1).trim();
            if (msg.isEmpty()) {
                sendMessage("System: usage /w NAME TEXT");
                return true;
            }

            boolean ok = server.whisper(toName, msg, this);
            if (!ok) {
                sendMessage("System: user not found: " + toName);
            } else {
                sendMessage("(whisper to " + toName + ") " + msg);
            }
            return true;
        }

        return false;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
