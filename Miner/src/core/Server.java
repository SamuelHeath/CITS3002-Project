package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 *
 * @author Samuel Heath
 */
public class Server implements Runnable {
    
    private static int NETWORK_PORT; // Port on which the server listens
    
    
    public Server(int NET_PORT) {
        NETWORK_PORT = NET_PORT;
        System.out.println("Starting Server on port: " + NET_PORT);
        run();
    }
    
    @Override
    public void run() {
        
        try {
        SSLServerSocketFactory ServerSockFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket ServerSock = (SSLServerSocket) ServerSockFactory.createServerSocket(NETWORK_PORT);
        SSLSocket clientSock = (SSLSocket) ServerSock.accept();
        
        InputStream inputstream = clientSock.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        
        String string = null;
        while ((string = bufferedreader.readLine()) != null) {
            System.out.println(string);
            System.out.flush();
        }
        
        } catch (IOException IOE) { IOE.printStackTrace(); }
        
    }
    
    /**
     * Broadcasts a message, to all connected clients, via SSL Sockets.
     * @param message 
     */
    public void broadcastMessage(String message) {
        
    }
    
    /**
     * @return              NETWORK_PORT, the port the server is running on.
     */
    public static int getPort() {
        return NETWORK_PORT;
    }
    
}