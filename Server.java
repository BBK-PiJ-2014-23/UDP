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

    public Server() {
        try {
            socket = new ServerSocket(PORT);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in Server() !!!!!");
        }
        lastClientId = 0;
        needsSender = true;
        queue = new LinkedList<ServerThread>();
    }

    public static void main(String[] args) {
        Server server = new Server();
        while(true) {
            server.listen();
        }
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
        ServerThread thread = null;
        try {
            thread = new ServerThread(this, client, lastClientId, needsSender);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in listen() !!!!!");
        }
        thread.start();
        if (needsSender) {
            needsSender = false;
        } else {
            queue.add(thread);
        }
    }

    public void storeData(String input) {
        test = input;
        System.out.println("Test contains: " + test);
    }

    public String getData() {
        return test;
    }

    public void requestNewSender() {
        if (queue.size() > 0) {
            ServerThread thread = queue.remove();
            thread.setToSender();
        }
    }
}