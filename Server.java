import java.io.*;
import java.net.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private Socket client;

    private int lastClientId;
    private boolean needsSender;
    
    private String test = "";

    public Server() throws IOException {
        socket = new ServerSocket(PORT);
        lastClientId = 0;
        needsSender = true;
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
        (new ServerThread(this, client, lastClientId, needsSender)).start();
        if (needsSender) {
            needsSender = false;
        }
    }
    
    public void storeData(String input) {
        test = input;
    }
    
    public String getData() {
        return test;
    }
}