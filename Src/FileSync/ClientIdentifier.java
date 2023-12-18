import java.io.Serializable;

public class ClientIdentifier implements Serializable{
    String id;

    public ClientIdentifier(String Id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
