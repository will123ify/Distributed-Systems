import java.io.BufferedReader;
import java.io.IOException;

    public class ServerListener implements Runnable {
    private BufferedReader reader;
    public ServerListener(BufferedReader reader) {
        this.reader = reader;
    }
    public void run() {
        try {
            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
