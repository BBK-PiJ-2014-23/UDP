import java.io.*;
import java.net.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private Socket client;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    private int lastClientId;
    private boolean needsSender;

    public Server() throws IOException {
        socket = new ServerSocket(PORT);
        lastClientId = 0;
        needsSender = true;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while(true) {
            server.listen();
            server.setupStreams();
            server.tellClientId();
            server.tellClientRole();
        }
    }

    public void listen() throws IOException {
        System.out.println("Server listening on port " + socket.getLocalPort());
        client = socket.accept();
        System.out.println("Client connected on port " + client.getPort());
    }

    public void setupStreams() throws IOException {
        inputStream = new DataInputStream(client.getInputStream());
        outputStream = new DataOutputStream(client.getOutputStream());
    }
    
    public void tellClientId() throws IOException {
        lastClientId++;
        System.out.println("Sending ID " + lastClientId + " to client");
        outputStream.writeInt(lastClientId);
    }
    
    public void tellClientRole() throws IOException {
        System.out.println("This needs a sender? " + needsSender);
        outputStream.writeBoolean(needsSender);
    }
}