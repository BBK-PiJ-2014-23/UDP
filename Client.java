import java.io.*;
import java.net.*;

public class Client
{
    private static final int SERVER_PORT = 2000;
    private static final String MULTICAST_IP = "224.0.0.3";
    private static final int MULTICAST_PORT = 8888;

    //constructor
    private String serverIp;
    private String fileName;
    private boolean isSender;

    //launch()
    private boolean isConnected;

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

    public Client(String serverIp, String fileName) {
        this.serverIp = serverIp;
        this.fileName = fileName;
        this.isSender = false;
    }

    public void launch() {
        connect();
        if (isConnected) {
            setupStreams();
            acceptClientId();
            while(true) {
                acceptRole();
                if (!isSender) {
                    launchAudioPlayer();
                    recieveData();
                } else {
                    sendData();
                }
            }
        }
    }

    public void setRole(boolean bool) {
        isSender = bool;
    }

    public void connect() {
        System.out.println("Connecting to server...");
        try {
            socket = new Socket(serverIp, SERVER_PORT);
            System.out.println("Successfully connected via port " + socket.getLocalPort());
            datagramSocket = new DatagramSocket();
            System.out.println("Connected via UDP on port " + datagramSocket.getLocalPort());
            serverAddress = InetAddress.getByName(serverIp);
            multicastAddress = InetAddress.getByName(MULTICAST_IP);
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(multicastAddress);
            isConnected = true;
        } catch (UnknownHostException host) {
            System.out.println("Server does not exist!");
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
            System.out.println("Server unavailable. Trying again...");
        }
        System.out.println("Recieved client id from server is " + clientId);
    }

    public void acceptRole() {
        try {
            isSender = inputStream.readBoolean();
        } catch (IOException io) {
            System.out.println("Server unavailable. Trying again...");
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
            byte[] data = new byte[2];
            packetFromServer = new DatagramPacket(data, data.length);
            multicastSocket.receive(packetFromServer);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in recieveData() !!!!!");
        }

        String test = new String(packetFromServer.getData());
        System.out.println("Recieved client " + test + "test data from server");
    }

    public void launchAudioPlayer() {
        (new AudioThread(fileName)).start();
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String serverIp = args[0];
            String fileName = args[1];
            File audioFile = new File(fileName);
            if (audioFile.exists()) {
                Client client = new Client(serverIp, fileName);
                client.launch();
            } else {
                System.out.println("Address of sound file is incorrect.");
            }
        } else {
            System.out.println("Please enter server IP and the address of a sound file");
        }
    }
}