import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.Buffer;

public class ChatClient {
    private String hostname;
    private int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    
    public ChatClient(String hostname, int port, String username) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
    }


    public void start() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected to the chat server");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            writer.println(username);

            // Create a new thread to listen for messages from the server
            new Thread(new ServerListener(reader)).start();

            // Loop to send messages to the server
            BufferedReader userInputBR = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputBR.readLine()) != null) {
                sendMessage(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (message.equalsIgnoreCase("/quit")) {
            stop();
            return;
        }
        writer.println(message);
    }
    

    public void stop() {
        try {
            System.out.println("Closing the connection to the server.");
            if (socket != null && !socket.isClosed()) {
                writer.println("Client is disconnecting.");
                socket.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String hostname = "localhost"; // or pass hostname via args
        int port = 12345; // or pass port via args

        try{
        // Prompt for username
        System.out.println("Enter your username:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String username = br.readLine();

        ChatClient client = new ChatClient(hostname, port, username);
        client.start();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error occured while reading username");
            System.exit(1);
        }
    }

}
