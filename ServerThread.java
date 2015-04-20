import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private static final int PORT = 2000;

    private Socket client;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private DatagramSocket datagramSocket;
    private DatagramPacket packetFromClient;

    private int clientId;
    private boolean needsSender;

    String test;

    public ServerThread(int clientId, boolean needsSender) throws IOException {
        super();
        this.clientId = clientId;
        needsSender = needsSender;
    }

    @Override
    public void run() {
        while(true) {
            setupStreams();
            tellClientId();
            tellClientRole();
            recieveData();
            sendData();
        }
    }

    public void setupStreams() {
        try {
            inputStream = new DataInputStream(client.getInputStream());
            outputStream = new DataOutputStream(client.getOutputStream());
            datagramSocket = new DatagramSocket(2000);
        } catch (IOException io) {}
    }

    public void tellClientId() {
        try {
            System.out.println("Sending ID " + clientId + " to client");
            outputStream.writeInt(clientId);
        } catch (IOException io) {}
    }

    public void tellClientRole() {
        try  {
            System.out.println("This needs a sender? " + needsSender);
            outputStream.writeBoolean(needsSender);
        } catch (IOException io) {}
    }

    public void recieveData() {
        try {
            byte[] data = new byte[2];
            packetFromClient = new DatagramPacket(data, data.length);
            datagramSocket.receive(packetFromClient);
            test = new String(packetFromClient.getData());
            System.out.println("Recieved test data from client " + test);
        } catch (IOException io) {}
    }

    public void sendData() {
        try {
            InetAddress ip = packetFromClient.getAddress();
            int port = packetFromClient.getPort();
            byte[] data = test.getBytes();
            DatagramPacket packetToClient = new DatagramPacket(data, data.length, ip, port);
            datagramSocket.send(packetToClient);
        } catch (IOException io) {}
    }
}