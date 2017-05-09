package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ServerSocketFactory;
import net.Message;

/**
 *
 * @author Samuel Heath
 */
public class Server implements Runnable {
    
    private static int NETWORK_PORT; // Port on which the server listens
    private static final ArrayList<ConnectionWorker> CONNECTIONS = new ArrayList(10);
    private ServerSocketFactory ServerSockFactory;
    private ServerSocket ServerSock;
    private Boolean server_stop = false;
    
    public Server(int NET_PORT) {
        NETWORK_PORT = NET_PORT;
        init();
    }
    
    @Override
    public void run() {
        
        System.out.println("Starting Server on port: " + NETWORK_PORT);
        Socket clientSock;
        
        while (hasStopped() != true) {
            
            try {
                clientSock = (Socket) ServerSock.accept();
                System.out.println("Made it");
                ConnectionWorker worker = new ConnectionWorker(clientSock);
                CONNECTIONS.add(worker);
                new Thread(worker).start();
        
            } catch (IOException IOE) { IOE.printStackTrace(); }
        }
        stopServer();
    }
        
    
    /**
     * Stops the server by closing all the ports.
     */
    private void stopServer() { 
        this.server_stop = true; 
        for (int i = 0; i < CONNECTIONS.size(); i++) { 
            CONNECTIONS.get(i).closeSocket(); 
            CONNECTIONS.remove(i);
            }
        try {
            this.ServerSock.close();
        } catch (IOException e) {}
    }
    
    /**
     * @return                  Whether the server has stopped.
     */
    private Boolean hasStopped() { return this.server_stop; }
    
    /**
     * Initialises the server socket.
     */
    private void init() {
        try {
            ServerSockFactory = (ServerSocketFactory) ServerSocketFactory.getDefault();
            ServerSock = (ServerSocket) ServerSockFactory.createServerSocket(NETWORK_PORT);
        } catch (IOException IOE) { IOE.printStackTrace(); }
    }
    
    /**
     * Broadcasts a message, to all connected clients, via SSL Sockets.
     * @param msg                   The message needing to be broadcasted.
     */
    public static void broadcastMessage(Message msg) {
        for (ConnectionWorker w:CONNECTIONS) {
            w.sendMessage(msg);
        }
    }
    
    public static ArrayList<ConnectionWorker> getConnections() { return CONNECTIONS; }
    
    /**
     * @return              NETWORK_PORT, the port the server is running on.
     */
    public int getPort() {
        return NETWORK_PORT;
    }
    
}