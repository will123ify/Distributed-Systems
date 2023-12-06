import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;


public class ChatServer {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService pool; // For handling multiple clients concurrently
    private Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) {
        this.port = port;
        pool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Chat Server started on port " + port);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            pool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    public void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                handler.sendMessage(message);
            }
        }
    }

    // Method to add a client handler to the set
    public void addClientHandler(ClientHandler handler) {
        synchronized (clientHandlers) {
            clientHandlers.add(handler);
        }
    }

    // Method to remove a client handler from the set
    public void removeClientHandler(ClientHandler handler) {
        synchronized (clientHandlers) {
            clientHandlers.remove(handler);
        }
    }


    // Entry point for the server
    public static void main(String[] args) {
        int port = 12345; // or pass port via args
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
