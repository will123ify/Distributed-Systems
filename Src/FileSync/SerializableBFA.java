import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;

public class SerializableBFA implements Serializable {
    private String name;
    private boolean isDirectory;
    private long size;
    private long creationTime;
    private long lastModifiedTime;
    private long lastAccessedTime;;


    public SerializableBFA(BasicFileAttributes attr, String name) {
        this.name = name;
        this.creationTime = attr.creationTime().toMillis();
        this.lastModifiedTime = attr.lastModifiedTime().toMillis();
        this.lastAccessedTime = attr.lastAccessTime().toMillis();
        this.isDirectory = attr.isDirectory();
        this.size = attr.size();

    }

    public long getSize() {
        return size;
    }
    

    public boolean isDirectory() {
        return isDirectory;
    }


    public long getCreationTime() {
        return creationTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public long getLastAccessTime() {
        return lastAccessedTime;
    }

    public String getName() {
        return name;
    }
}