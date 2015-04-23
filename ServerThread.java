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

    public ServerThread(Server server, DatagramSocket datagramSocket, Socket client, int clientId, boolean connectedToSender) {
        super();
        this.server = server;
        this.datagramSocket = datagramSocket;
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
        } catch (IOException io) {
            System.out.println("!!!!! IOException in setupStreams() !!!!!");
        }
    }

    public void tellClientId() {
        try {
            System.out.println("Sending ID " + clientId + " to client");
            outputStream.writeInt(clientId);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in tellClientId() !!!!!");
        }
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
        byte[] data = new byte[2];
        System.out.println("Recieved test data from client " + server.getData());
        while(true) {
            packetFromClient = new DatagramPacket(data, data.length);

            try{
                datagramSocket.receive(packetFromClient);
            } catch (SocketTimeoutException timeout) {
                System.out.println("Sender timeout.");
                connectedToSender = false;
                server.requestNewSender();
            } catch (IOException io) {
                System.out.println("!!!!! IOException in recieveData() !!!!!");
            }
            server.storeData(new String(packetFromClient.getData()));
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