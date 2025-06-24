import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.net.*;
import java.io.*;
import socketutils.MessageHandler;

public class MyServer {

    public static ArrayList<ClientHandler> clientList = new ArrayList<>(0);    
    public static int port = 1234;
    public static int id = 0;
    public static volatile boolean shouldStop = false;

    public static void main(String[] args){

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{

            System.out.println("Closing the Server.....");;
            shouldStop = true;
            clientList.forEach((client) ->{
                client.closeAndCleanup();
            });



        }));

        try(ServerSocket serverSocket = new ServerSocket(port);) {

            System.out.println("Server is listening on port: " + port);

            while (!shouldStop) {

                Socket clientSocket = serverSocket.accept();

                id++;

                ClientHandler clientHandler = new ClientHandler(clientSocket, id);
                new Thread(clientHandler).start();

                clientList.add(clientHandler);

                System.out.println("User added");
                System.out.println("Client id: " + id);


            }

        }catch (Exception e){
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } 

    }
    
}


class ClientHandler implements Runnable{

    Socket clientSocket;
    int id;
    ObjectInputStream reader;
    String username;
    BufferedReader textReader;
    ObjectOutputStream out;
    Thread thread;

    ClientHandler(Socket socket, int userId){

        this.clientSocket = socket;
        this.id = userId;
        this.username = "";

        try {

            // object output stream
            this.out = new ObjectOutputStream(clientSocket.getOutputStream()); // output stream before input else it crashes without giving an exception

            //object reader
            this.reader = new ObjectInputStream(clientSocket.getInputStream());

           
            
        } catch (IOException e) {

            System.out.println("Error whilst setting up communcation with client");
            e.printStackTrace();

        }

    }
    public void closeAndCleanup(){
        
        try{

            this.clientSocket.close();
            this.out.close();
            this.reader.close();
            this.thread.interrupt();


            }catch(IOException e){
                System.out.println("Error whilst closing client handler");
                e.printStackTrace();
            }

    }

    public void sendMessageObject(Message messageObj){

        try {

            out.writeObject(messageObj);
            
        } catch (IOException e) {

            System.out.println("Error whilst transmitting message to client. Closing client socket.");
            closeAndCleanup();

        }
        

    }

    @Override
    public void run(){

        //the thread this is running on
        this.thread = Thread.currentThread();
        
        while (true){

            // listen and transmit
            Message recieved = MessageHandler.readMessage(reader,Message.class);

            if (recieved == null){

                // incorrect data means something wrong with the client socket hence needs closing
                closeAndCleanup();
                break;

            }else if(recieved.message.equals("usernamecheck")){

                AtomicBoolean approved = new AtomicBoolean(true);
                

                MyServer.clientList.forEach((client) ->{
                    if(client.username.equals(recieved.sender) && client.id != this.id){
                        approved.set(false);
                    }
                });

                if (approved.get()){
                    sendMessageObject(new Message("usernameapproved200", recieved.sender));
                    this.username = recieved.sender;
                }else{
                    sendMessageObject(new Message("usernamerejected404", recieved.sender));
                }
                

            }else{

                MyServer.clientList.forEach((client) ->{
                    
                    if(client.id != this.id){

                        client.sendMessageObject(recieved);

                    }
                });
            } 
        }            
    }
}

