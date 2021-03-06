package pm85.chat.server;

import pm85.chat.network.Connection;
import pm85.chat.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {

    private final List<Connection> connections = new ArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try (ServerSocket serverSocket = new ServerSocket(2002)) {
            System.out.println("Server has been started...");
            while (true) {
                try {
                    new Connection(serverSocket.accept(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        System.out.println("Client connected   " + connection);
        connections.add(connection);
        sendMessageToAll("Client connected   " + connection);
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String string) {
        System.out.println("Receive string: " + string);
        sendMessageToAll(string);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        System.out.println("Client disconnected   " + connection);
        connections.remove(connection);
        sendMessageToAll("Client disconnected   " + connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        e.printStackTrace();
    }

    private void sendMessageToAll(String message) {
        for (Connection connection : connections) {
            connection.sendMessage(message);
        }
    }

}
