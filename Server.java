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
        if (queue.size() > 0) {
            ServerThread thread = queue.remove();
            thread.makeClientSender();
        } else {
            needsSender = true;
        }
    }
    
    public void storeData(String data) {
        test = data;
    }

    public String getData() {
        return test;
    }

    public static void main(String[] args) {
        Server2 server = new Server2();
        server.launch();
    }
}