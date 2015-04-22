import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
    private static final int RECIEVER_PORT = 2000;
    private static final String MULTICAST_ADDRESS = "224.0.0.3";
    private static final int MULTICAST_PORT = 8888;

    private Socket client;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private DatagramSocket datagramSocket;
    private DatagramPacket packetFromClient;

    private int clientId;
    private boolean connectedToSender;

    private Server server;

    public ServerThread(Server server, Socket client, int clientId, boolean connectedToSender) throws IOException {
        super();
        this.server = server;
        this.client = client;
        this.clientId = clientId;
        this.connectedToSender = connectedToSender;
    }

    @Override
    public void run() {
        setupStreams();
        tellClientId();
        tellClientRole();

        while(true) {
            if (connectedToSender) {
                recieveData();
            } else {
                sendData();
            }
        }
    }

    public void setupStreams() {
        try {
            inputStream = new DataInputStream(client.getInputStream());
            outputStream = new DataOutputStream(client.getOutputStream());
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
            System.out.println("This needs a sender? " + connectedToSender);
            outputStream.writeBoolean(connectedToSender);
        } catch (IOException io) {}
    }

    public void recieveData() {
        try {
            datagramSocket = new DatagramSocket(RECIEVER_PORT);

            byte[] data = new byte[8];
            packetFromClient = new DatagramPacket(data, data.length);

            datagramSocket.receive(packetFromClient);
            server.storeData(new String(packetFromClient.getData()));

            System.out.println("Recieved test data from client " + server.getData());
        } catch (IOException io) {}
    }

    public void sendData() {
        try {
            InetAddress addr = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramSocket serverSocket = new DatagramSocket();

            byte[] data = server.getData().getBytes();
            DatagramPacket packetToClient = new DatagramPacket(data, data.length, addr, MULTICAST_PORT);

            serverSocket.send(packetToClient);
            System.out.println("Sent test data from client " + server.getData() + " to client " + clientId);
        } catch (IOException io) {}
    }
}