package socketutils;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageHandler {

    public static <T> T readMessage(ObjectInputStream in, Class<T> type){

        try{

            Object obj = in.readObject();
            return type.cast(obj); 

        }catch(ClassCastException | ClassNotFoundException | IOException e){

            return null;

        }

    }   
    
}

