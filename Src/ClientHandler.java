import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

    public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;


    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            // Create the streams to listen to the client messages and respond back
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            this.username = in.readLine();
            System.out.println(username + " has connected to the server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            server.addClientHandler(this);
            String clientMessageToServer;

            // Listen for client messages
            while ((clientMessageToServer = in.readLine()) != null) {
                // Which user sent the message
                String clientMessageBroadcast = username + ": " + clientMessageToServer;

                // Broadcast the message to other clients
                server.broadcastMessage(clientMessageBroadcast);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClientHandler(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Broadcasting disconnection message
            server.broadcastMessage(username + " has disconnected.");
        }
    }


    // Method to send messages to this client
    public void sendMessage(String message) {
        out.println(message);
    }
}