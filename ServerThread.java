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

        while(true) {
            tellClientRole();
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
            //System.out.println("This needs a sender? " + connectedToSender);
            outputStream.writeBoolean(connectedToSender);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in tellClientRole() !!!!!");
        }
    }

    public void recieveData() {
        try {
            datagramSocket = new DatagramSocket(RECIEVER_PORT);
        } catch (SocketException socket) {
            System.out.println("!!!!! SocketException in recieveData() !!!!!");
        }

        byte[] data = new byte[2];

        while(true) {
            packetFromClient = new DatagramPacket(data, data.length);

            try{
                datagramSocket.receive(packetFromClient);
            } catch (IOException io) {
                System.out.println("!!!!! IOException in recieveData() !!!!!");
            }
            server.storeData(new String(packetFromClient.getData()));

            System.out.println("Recieved test data from client " + server.getData());
            break;
        }
    }

    public void sendData() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException host) {
            System.out.println("!!!!! UnknownHostException in sendData() !!!!!");
        }
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket();
        } catch (SocketException socket) {
            System.out.println("!!!!! SocketException in sendData() !!!!!");
        }

        byte[] data = server.getData().getBytes();
        //while(true) {
        DatagramPacket packetToClient = new DatagramPacket(data, data.length, addr, MULTICAST_PORT);
        try {
            serverSocket.send(packetToClient);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in sendData() !!!!!");
        }
        System.out.println("Sent test data from client " + server.getData() + " to client " + clientId);
        //}
        try {
            this.sleep(2000);
        } catch (InterruptedException interrupt) {}
    }

    public void setToSender() {
        connectedToSender = true;
    }
}