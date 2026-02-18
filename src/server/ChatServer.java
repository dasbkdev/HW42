package server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, ClientHandler> byName = new ConcurrentHashMap<>();

    public void addClient(ClientHandler client) {
        clients.add(client);
        byName.put(client.getName(), client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        byName.remove(client.getName(), client);
        broadcast("System: " + client.getName() + " left the chat", client);
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public boolean isNameFree(String name) {
        return !byName.containsKey(name);
    }

    public boolean rename(ClientHandler client, String newName) {
        String old = client.getName();

        if (newName == null) return false;
        newName = newName.trim();

        if (newName.isEmpty()) return false;
        if (newName.contains(" ")) return false;
        if (!isNameFree(newName)) return false;


        byName.remove(old, client);
        client.setName(newName);
        byName.put(newName, client);

        broadcast("System: " + old + " is now " + newName, client);
        return true;
    }

    public String listUsers() {
        return String.join(", ", byName.keySet());
    }

    public boolean whisper(String toName, String message, ClientHandler from) {
        ClientHandler to = byName.get(toName);
        if (to == null) return false;

        to.sendMessage("(whisper) " + from.getName() + ": " + message);
        return true;
    }
}
