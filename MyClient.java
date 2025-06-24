import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


import socketutils.MessageHandler;

public class MyClient{

    public static int port = 1234;
    public static String serverAddress = "192.168.18.61";
    public static String[] username = {""};
    public static Scanner userInput = new Scanner(System.in);
    public static Thread writingThread;
    public static Thread listenerThread;


    public static volatile boolean shouldStop = false;

    public static void setUsername(ObjectOutputStream out, ObjectInputStream in, Scanner userIn) throws IOException{

        AtomicBoolean approved = new AtomicBoolean(false);
        Message usernameResponse;

        while (!approved.get()){

             try{

                System.out.println("Enter an appropriate username.");
                String temp = userIn.nextLine();

                out.writeObject(new Message("usernamecheck", temp));
                out.flush();

                usernameResponse = socketutils.MessageHandler.readMessage(in, Message.class);

                if (usernameResponse == null){
                    
                    System.out.println("Could not recieve response from server");

                    throw new IOException();
                    
                }else if (usernameResponse.message.equals("usernameapproved200")) {
                    username[0] = usernameResponse.sender;
                    approved.set(true);
                }else if (usernameResponse.message.equals("usernamerejected404")) {

                    System.out.println("Username not valid try another name");
                    
                }

            }catch(IOException e){
                System.out.println("Could not send message. Server might be down.");
                throw new IOException();
            }

        }
                
    }

    public static void main(String[] args){

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            shouldStop = true;
            System.out.println("\nShutting Client down....");

            if (writingThread != null){

                writingThread.interrupt();
                listenerThread.interrupt();

            }
            
        }));

        // constant connection
        while (!shouldStop){

            try
            ( 
                Socket socket = new Socket(serverAddress, port);

                ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());

            ) {

                System.out.println("Connected to Server!");

                setUsername(writer, reader, userInput); // handles setting the username up

                if(shouldStop == true){
                    break;
                }

                // writing thread
                writingThread = new Thread(()->{

                    while(!shouldStop){
                        System.out.println("Message: ");

                        try{

                            if (!userInput.hasNextLine()){

                                writingThread.interrupt();
                                break;

                            }else{

                                writer.writeObject(new Message(userInput.nextLine(), username[0])); // figure out username system later
                                writer.flush();

                            }
                            
                        }catch(IOException e){
                            System.out.println("Could not send message. Server might be down.");
                            break;
                        }
                    
                    }

                });
                writingThread.start();


               
                // listening thread
                listenerThread  = new Thread(()-> {

                    while (!shouldStop){

                        Message recievedMessage = MessageHandler.readMessage(reader, Message.class);
                        
                        if (recievedMessage == null){

                            System.out.println("Could not decipher message from server"); // figure out how to get lost message later
                            shouldStop = true;
                            break; // exist loop and considering that no messages will be recieved server will close the socket
                            
                        }else{

                            System.out.println(recievedMessage.sender + ": " + recievedMessage.message);

                        }

                    }
                });
                listenerThread.start();

                if(!writingThread.isAlive() || !listenerThread.isAlive()){
                    throw new IOException();
                }
                

                try{
                    writingThread.join();
                    listenerThread.join();
                }catch(InterruptedException ignored){
                    System.out.println("Thread was interrupted");
                    ignored.printStackTrace();
                }
                
                

            } catch(IOException e){
                
                System.out.println("Connection to server not possible. Retrying connection in 3 seconds....");

                try{

                    Thread.sleep(3000);

                }catch(InterruptedException ignored){
                }
                
            }

            if (shouldStop){
                    break;
            }
        }
        userInput.close();
    }

}

