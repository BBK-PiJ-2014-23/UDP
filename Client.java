import java.io.*;
import java.net.*;

public class Client
{
    private static final String SERVER_IP = "127.0.0.1";
    private static final String SERVER_NAME = "localhost";
    private static final int SERVER_PORT = 2000;

    private static final String MULTICAST_ADDRESS = "224.0.0.3";
    private static final int MULTICAST_PORT = 8888;

    private Socket socket;

    private DatagramSocket datagramSocket;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private int clientId;
    private boolean isSender;

    public Client() throws UnknownHostException, IOException {
        isSender = false;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Client client = new Client();
        client.connect();
        client.setupStreams();
        client.acceptClientId();

        while(true) {
            client.acceptRole();
            if (client.isSender) {
                client.sendData();
            } else {
                client.recieveData();
                client.playAudio();
            }
        }
    }

    public void connect() {
        System.out.println("Connecting to server...");
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Successfully connected via port " + socket.getLocalPort());
            datagramSocket = new DatagramSocket();
            System.out.println("Connected via UDP on port " + datagramSocket.getLocalPort());
        } catch (UnknownHostException host) {
            System.out.println("!!!!! UnknownHostException in connect() !!!!!");
            connect();
        } catch (IOException io) {
            System.out.println("Server unavailable. Trying again...");
            connect();
        }
    }

    public void setupStreams() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException io) {
            System.out.println("!!!!! IOException in setupStreams() !!!!!");
        }
    }

    public void acceptClientId() {
        try {
            clientId = inputStream.readInt();
        } catch (IOException io) {
            System.out.println("!!!!! IOException in acceptClientId() !!!!!");
        }
        System.out.println("Recieved client id from server is " + clientId);
    }

    public void acceptRole() {
        try {
            isSender = inputStream.readBoolean();
        } catch (IOException io) {
            System.out.println("!!!!! IOException in acceptRole() !!!!!");
        }
        System.out.println("Client is a sender? " + isSender);
    }

    public void sendData() {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(SERVER_NAME);
        } catch (UnknownHostException host) {
            System.out.println("!!!!! UnknownHostException in sendData() !!!!!");
        }

        byte[] data = Integer.toString(clientId).getBytes();
        while(true) {
            DatagramPacket packetToServer = new DatagramPacket(data, data.length, address, 2000);
            try {
                datagramSocket.send(packetToServer);
            } catch (IOException io) {
                System.out.println("!!!!! IOException in sendData() !!!!!");
            }
        }
    }

    public void recieveData() {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(MULTICAST_ADDRESS);
        } catch (UnknownHostException host) {
            System.out.println("!!!!! UnknownHostException in recieveData() !!!!!");
        }

        MulticastSocket clientSocket = null;
        DatagramPacket packetFromServer = null;

        try {
            clientSocket = new MulticastSocket(MULTICAST_PORT);
            clientSocket.joinGroup(address);

            byte[] data = new byte[2];
            packetFromServer = new DatagramPacket(data, data.length);

            clientSocket.receive(packetFromServer);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in recieveData() !!!!!");
        }

        String test = new String(packetFromServer.getData());
        System.out.println("Recieved client " + test + "test data from server");
    }

    public void playAudio() {

    }
}