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