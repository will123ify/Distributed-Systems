import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSyncServer {
    private static ServerSocket socket;
    private static String clientID;
    public static Set<SerializableBFA> readStream() {
        try {
            // start tcp connection
            socket = new ServerSocket(1234);
            while (!socket.isClosed()) {
                System.out.println("Server connection open");
                Socket client = socket.accept();
                // Get inputstream
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                // Deserialize
                var attributes = (Set<SerializableBFA>) in.readObject();
                clientID = (String) in.readObject();
                System.out.println("Successfully deserialized");
                in.close();
                client.close();
                return attributes;
            }
        } catch (ClassNotFoundException | IOException io) {
            io.printStackTrace();
            System.out.println("Failed to deserialize");
        }
        return null;
    }

    public static void main(String[] args) {
        var attributes = readStream();
        String directory = "C:\\Users\\Jimmy\\VSC\\FileSync\\Serverfiles\\Client" + clientID + "\\";
        var currentServerFiles = serverFileset(directory);
        var nameList = compareSBFA(currentServerFiles, attributes);
        System.out.println("Communicating with: " + clientID);
        //Printing out the files that need to be synced
        for (String name : nameList) {
            System.out.println("File: " + name + " needs to be synced");
        }

    }

    public static boolean compareAttributes(SerializableBFA a1, SerializableBFA a2) {
        //A cooler way to return a boolean
        var variable = (a1.getName().equals(a2.getName()) &&
                a1.getSize() == a2.getSize() &&
                a1.getLastModifiedTime() == a2.getLastModifiedTime()) ? true : false;
        return variable;
    }

    public static List<String> compareSBFA(Set<SerializableBFA> server, Set<SerializableBFA> client) {
        List<String> nameList = new ArrayList<String>();
        for (SerializableBFA clientAttribute : client) {
            // Flag for checking if the file exists or is modified
            boolean existsOrMatched = false;
            for (SerializableBFA serverAttribute : server) {
                // Compare the attributes of the files
                if (compareAttributes(clientAttribute, serverAttribute)) {
                    // If file already exists or is already synced 
                    existsOrMatched = true;
                }
            }
            if (!existsOrMatched) {
                nameList.add(clientAttribute.getName());
            }
        }
        if (!nameList.isEmpty()) {
            return nameList;
        }
        System.out.println("Server is up to date");
        return null;
    }

    public static Set<SerializableBFA> serverFileset(String directory) {
        try {
            Set<SerializableBFA> attributes = new HashSet<SerializableBFA>();
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory));

            for (Path filePath : stream) {
                var attribute = Files.readAttributes(filePath, BasicFileAttributes.class);
                attributes.add(new SerializableBFA(attribute, filePath.getFileName().toString()));
            }
            return attributes;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }
    
}
