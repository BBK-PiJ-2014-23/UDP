import java.io.*;
import java.net.*;

public class Client
{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2000;

    private Socket socket;
    
    private DatagramSocket datagramSocket;
    InetAddress ip;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private int clientId;
    private boolean isSender;

    public Client() {
        isSender = false;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client();
        client.connect();
        client.setupStreams();
        client.acceptClientId();
        client.acceptRole();
        client.sendData();
    }

    public void connect() throws UnknownHostException, IOException {
        System.out.println("Connecting to server...");
        socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Successfully connected via port " + socket.getPort());
        datagramSocket = new DatagramSocket();
        System.out.println("Connected via UDP on port " + datagramSocket.getLocalPort());
        ip = InetAddress.getByName(SERVER_IP);
    }

    public void setupStreams() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void acceptClientId() throws IOException {
        clientId = inputStream.readInt();
        System.out.println("Recieved client id from server is " + clientId);
    }

    public void acceptRole() throws IOException {
        isSender = inputStream.readBoolean();
        System.out.println("Client is a sender? " + isSender);
    }

    public void sendData() throws IOException {
        byte[] test = Integer.toString(clientId).getBytes();
        DatagramPacket packetToServer = new DatagramPacket(test, test.length, ip, 2000);
        datagramSocket.send(packetToServer);
    }
}