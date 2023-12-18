import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class Client {
    String identifier;
    public static Set<SerializableBFA> clientFile(String directory) {
        try {
            // Reading the file attributes in given directory
            Set<SerializableBFA> attributes = new HashSet<SerializableBFA>();
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory));
            for (Path filePath : stream) {
                var attribute = Files.readAttributes(filePath, BasicFileAttributes.class);
                // Adding the attributes to a list
                attributes.add(new SerializableBFA(attribute, filePath.getFileName().toString()));
            }
            return attributes;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    
    public static void main(String[] args) {
        //Specify which directory you want to send
        String directory = "C:\\Users\\Jimmy\\VSC\\FileSync\\Clientfiles\\";
        //Your ID
        String id = "1";
        var attributes = clientFile(directory);
        sendStream(attributes,id);
    }

    public static void sendStream(Set<SerializableBFA> attributes, String clientID) {

        try (Socket socket = new Socket("localhost", 1234);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            // Serializing the set of attributes
            out.writeObject(attributes);
            out.writeObject(clientID);
            out.close();
            socket.close();
            System.out.println("Successfully serialized");
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("Failed to serialize");
        }
    }
}
