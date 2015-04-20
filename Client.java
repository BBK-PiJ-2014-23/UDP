import java.io.*;
import java.net.*;

public class Client
{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2000;

    private Socket socket;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    private boolean isSender;

    public Client() {
        isSender = false;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client();
        client.connect();
        client.setupStreams();
        client.acceptRole();
    }

    public void connect() throws UnknownHostException, IOException {
        System.out.println("Connecting to server...");
        socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Successfully connected via port " + socket.getPort());
    }

    public void setupStreams() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void acceptRole() throws IOException {
        isSender = inputStream.readBoolean();
        System.out.println("Client is a sender? " + isSender);
    }
}