import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private Socket client;

    private int lastClientId;
    private boolean needsSender;
    
    private Queue<ServerThread> queue;
    
    private String test = "";

    public Server() throws IOException {
        socket = new ServerSocket(PORT);
        lastClientId = 0;
        needsSender = true;
        queue = new LinkedList<ServerThread>();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while(true) {
            server.listen();
        }
    }

    public void listen() throws IOException {
        //System.out.println("Server listening on port " + socket.getLocalPort());
        client = socket.accept();
        System.out.println("Client connected on port " + client.getPort());
        lastClientId++;
        ServerThread thread = new ServerThread(this, client, lastClientId, needsSender);
        thread.start();
        if (needsSender) {
            needsSender = false;
        } else {
            queue.add(thread);
        }
    }
    
    public void storeData(String input) {
        test = input;
    }
    
    public String getData() {
        return test;
    }
    
    public void requestNewSender() {
        ServerThread thread = queue.remove();
        thread.setToSender();
    }
}