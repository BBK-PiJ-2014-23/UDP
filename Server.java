import java.io.*;
import java.net.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private Socket client;
    
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public Server() throws IOException {
        socket = new ServerSocket(PORT);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while(true) {
            server.listen();
            server.setupStreams();
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
}