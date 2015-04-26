import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server
{
    private static final int PORT = 2000;
    private static final String MULTICAST_IP = "224.0.0.3";
    private static final int MULTICAST_PORT = 8888;

    //constructor
    private int lastClientId;
    private boolean needsSender;
    private BlockingQueue<ClientThread> clientThreads;

    //launch()
    private ServerSocket serverSocket;
    private DatagramSocket senderClientSocket;
    private InetAddress multicastAddress;
    private DatagramSocket multicastSocket;
    private ListenerThread listenerThread;

    //test
    private String test = "";

    public Server() {
        lastClientId = 0;
        needsSender = true;
        clientThreads = new PriorityBlockingQueue<ClientThread>();
    }

    public void launch() {
        try {
            serverSocket = new ServerSocket(PORT);
            senderClientSocket = new DatagramSocket(PORT);
            senderClientSocket.setSoTimeout(1000);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in launch() !!!!!");
        }
        try {
            multicastAddress = InetAddress.getByName(MULTICAST_IP);
        } catch (UnknownHostException host) {
            System.out.println("!!!!! UnknownHostException in launch() !!!!!");
        }
        try {
            multicastSocket = new DatagramSocket();
        } catch (SocketException socket) {
            System.out.println("!!!!! SocketException in launch() !!!!!");
        }
        listenerThread = new ListenerThread(this, serverSocket);
        listenerThread.start();

        while(true) {
            if(needsSender) {
                requestNewSender();
            } else {
                recieveData();
                sendData();
            }
        }
    }

    public void requestNewSender() {
        try {
            while(needsSender) {
                //This will block until a client thread can be retrieved
                ClientThread thread = clientThreads.take();
                thread.makeClientSender();
                needsSender = false;
            }
        } catch (InterruptedException interrupt) {
            System.out.println("!!!!! InterruptedException in requestNewSender() !!!!!");
        }
    }

    public void recieveData() {
        byte[] data = new byte[2];
        System.out.println("Recieved test data from client " + getData());

        DatagramPacket packetFromClient = new DatagramPacket(data, data.length);

        try{
            senderClientSocket.receive(packetFromClient);
            storeData(new String(packetFromClient.getData()));
        } catch (SocketTimeoutException timeout) {
            System.out.println("Sender timeout.");
            needsSender = true;
        } catch (IOException io) {
            System.out.println("!!!!! IOException in recieveData() !!!!!");
        }
    }

    public void sendData() {
        byte[] data = getData().getBytes();
        DatagramPacket packetToClient = new DatagramPacket(data, data.length, multicastAddress, MULTICAST_PORT);
        try {
            multicastSocket.send(packetToClient);
        } catch (IOException io) {
            System.out.println("!!!!! IOException in sendData() !!!!!");
        }
        System.out.println("Sent test data from client " + getData() + " to clientThreads.");
    }

    public void storeData(String data) {
        test = data;
    }

    public String getData() {
        return test;
    }

    public void addClientThread(Socket clientSocket) {
        lastClientId++;
        ClientThread thread = new ClientThread(clientSocket, lastClientId);
        clientThreads.add(thread);
        thread.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.launch();
    }
}