import java.io.*;
import java.net.*;

public class Client
{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2000;
    private static final String SERVER_NAME = "localhost";
    private static final String MULTICAST_IP = "224.0.0.3";
    private static final int MULTICAST_PORT = 8888;

    //constructor
    private boolean isSender;

    //connect()
    private Socket socket;
    private DatagramSocket datagramSocket;
    private InetAddress serverAddress;
    private InetAddress multicastAddress;
    private MulticastSocket multicastSocket;

    //setupStreams()
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    //acceptClientId()
    private int clientId;

    public Client() {
        isSender = false;
    }

    public void launch() {
        connect();
        setupStreams();
        acceptClientId();

        while(true) {
            acceptRole();
            if (isSender) {
                sendData();
            } else {
                recieveData();
                playAudio();
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
            serverAddress = InetAddress.getByName(SERVER_NAME);
            multicastAddress = InetAddress.getByName(MULTICAST_IP);
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
        byte[] data = Integer.toString(clientId).getBytes();
        while(true) {
            DatagramPacket packetToServer = new DatagramPacket(data, data.length, serverAddress, 2000);
            try {
                datagramSocket.send(packetToServer);
            } catch (IOException io) {
                System.out.println("!!!!! IOException in sendData() !!!!!");
            }
        }
    }

    public void recieveData() {
        DatagramPacket packetFromServer = null;
        try {
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(multicastAddress);

            byte[] data = new byte[2];
            packetFromServer = new DatagramPacket(data, data.length);

            multicastSocket.receive(packetFromServer);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in recieveData() !!!!!");
        }

        String test = new String(packetFromServer.getData());
        System.out.println("Recieved client " + test + "test data from server");
    }

    public void playAudio() {

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.launch();
    }
}