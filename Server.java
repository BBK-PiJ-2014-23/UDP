import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private DatagramSocket datagramSocket;
    
    private Socket client;
    
    private int lastClientId;
    private boolean needsSender;

    private Queue<ServerThread> queue;

    private String test = "";

    public Server() {
        try {
            socket = new ServerSocket(PORT);
            datagramSocket = new DatagramSocket(PORT);
            datagramSocket.setSoTimeout(1000);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in Server() !!!!!");
        }
        lastClientId = 0;
        needsSender = true;
        queue = new LinkedList<ServerThread>();
    }

    public void listen() {
        //System.out.println("Server listening on port " + socket.getLocalPort());
        try {
            client = socket.accept();
        } catch (IOException io) {
            System.out.println("!!!!! IOException in listen() !!!!!");
        }
        System.out.println("Client connected on port " + client.getPort());
        lastClientId++;
        ServerThread thread = new ServerThread(this, datagramSocket, client, lastClientId, needsSender);
        thread.start();
        if (needsSender) {
            needsSender = false;
        } else {
            queue.add(thread);
        }
    }

        public void requestNewSender() {
        if (queue.size() > 0) {
            ServerThread thread = queue.remove();
            thread.makeClientSender();
        } else {
            needsSender = true;
        }
    }
    
    public void storeData(String data) {
        test = data;
    }

    public String getData() {
        return test;
    }

    public static void main(String[] args) {
        Server server = new Server();
        while(true) {
            server.listen();
        }
    }
}