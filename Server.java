import java.io.*;
import java.net.*;

public class Server
{
    private static final int PORT = 2000;

    private ServerSocket socket;
    private Socket client;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private DatagramSocket datagramSocket;
    private DatagramPacket packetFromClient;

    private int lastClientId;
    private boolean needsSender;

    String test;

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
            server.recieveData();
            server.sendData();
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
        datagramSocket = new DatagramSocket(2000);
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

    public void recieveData() throws IOException {
        byte[] data = new byte[2];
        packetFromClient = new DatagramPacket(data, data.length);
        datagramSocket.receive(packetFromClient);
        test = new String(packetFromClient.getData());
        System.out.println("Recieved test data from client " + test);
    }

    public void sendData() throws IOException {
        InetAddress ip = packetFromClient.getAddress();
        int port = packetFromClient.getPort();
        byte[] data = test.getBytes();
        DatagramPacket packetToClient = new DatagramPacket(data, data.length, ip, port);
        datagramSocket.send(packetToClient);
    }
}