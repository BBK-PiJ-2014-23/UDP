import java.io.*;
import java.net.*;

public class Server
{
    private static final int PORT = 2000;
    
    private ServerSocket socket;
    
    public Server() throws IOException {
        socket = new ServerSocket(PORT);
    }
    
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        
    }
}