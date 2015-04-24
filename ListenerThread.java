import java.io.*;
import java.net.*;
import java.util.*;

public class ListenerThread extends Thread {
    private Server server;
    private ServerSocket socket;

    public ListenerThread(Server server, ServerSocket socket) {
        super();
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = socket.accept();
            } catch (IOException io) {
                System.out.println("!!!!! IOException in listen() !!!!!");
            }
            System.out.println("Client connected on port " + clientSocket.getPort());
            server.addClientThread(clientSocket);
        }
    }
}