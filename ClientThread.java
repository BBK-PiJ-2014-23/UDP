import java.io.*;
import java.net.*;

public class ClientThread extends Thread implements Comparable<ClientThread>
{
    //constructor
    private int clientId;
    private boolean hasSenderClient;
    private boolean isClientAlive;
    private Socket clientSocket;

    //setupStreams()
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientThread(Socket clientSocket, int clientId) {
        super();
        this.clientId = clientId;
        this.clientSocket = clientSocket;
        this.isClientAlive = true;
    }

    @Override
    public synchronized void run() {
        setupStreams();  
        sendClientId();
        sendClientRole();
        while(isClientAlive) {
            try {              
                wait();
            } catch (InterruptedException interrupt) {
                System.out.println("!!!!! InterruptedException in run() !!!!!");
            }
            sendClientRole();
        } 
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException io) {
            System.out.println("!!!!! IOException in run() !!!!!");
        }
    }

    public void setupStreams() {
        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException io) {
            System.out.println("!!!!! IOException in setupStreams() !!!!!");
        }
    }

    public void sendClientId() {
        try {
            System.out.println("Sending ID " + clientId + " to client");
            outputStream.writeInt(clientId);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in sendClientId() !!!!!");
            isClientAlive = false;
            notify();
        }
    }

    public synchronized void sendClientRole() {
        try  {
            outputStream.writeBoolean(hasSenderClient);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in sendClientRole() !!!!!");
            isClientAlive = false;
            notify();
        }
    }

    public synchronized void makeClientSender() {
        hasSenderClient = true;
        notify();
    }

    @Override
    public int compareTo(ClientThread thread){
        return -1;
    }
}