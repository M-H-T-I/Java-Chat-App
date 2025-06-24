import java.io.Serializable;


public class Message implements Serializable {

    private static final long serialVersionUID = 1L; // required for correct serialization

    public String sender;
    public String message;

    Message(String message, String sender){
        this.message = message;
        this.sender = sender;
    }

}


