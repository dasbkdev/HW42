package server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientConnection {

    private final Socket socket;

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (socket;
             Scanner reader = getReader(socket);
             PrintWriter writer = getWriter(socket)) {

            send("Hello! Type messages. 'bye' to disconnect.", writer);

            while (true) {
                String msg;
                try {
                    msg = reader.nextLine();
                } catch (NoSuchElementException ex) {

                    break;
                }

                if (isEmpty(msg)) {
                    continue;
                }

                if (isBye(msg)) {
                    send("bye", writer);
                    break;
                }

                send(msg, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Scanner getReader(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        return new Scanner(isr);
    }

    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        return new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
    }

    private static void send(String response, PrintWriter writer) {
        writer.println(response);
        writer.flush();
    }

    private static boolean isBye(String msg) {
        return "bye".equalsIgnoreCase(msg.trim());
    }

    private static boolean isEmpty(String msg) {
        return msg == null || msg.isBlank();
    }
}
