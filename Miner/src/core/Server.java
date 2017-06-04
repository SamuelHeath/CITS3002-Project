package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Samuel Heath & Nerces
 */
public class Server implements Runnable {
    
    private static int NETWORK_PORT; // Port on which the server listens
    private static final ArrayList<ConnectionWorker> CONNECTIONS = new ArrayList(10);
    private SSLServerSocketFactory ServerSockFactory;
    private SSLServerSocket ServerSock;
    private Boolean server_stop = false;
    
    public Server(int NET_PORT) {
        NETWORK_PORT = NET_PORT;
        init();
    }
    
    @Override
    public void run() {
        
        System.out.println("Starting Server on port: " + NETWORK_PORT);
        SSLSocket clientSock;
        
        while (hasStopped() != true) {
            
            try {
                clientSock = (SSLSocket) ServerSock.accept();
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
            
            
            System.setProperty("javax.net.ssl.keyStore","keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword","password");
            System.setProperty("javax.net.ssl.trustStore","miner.jks");

            SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            
            ServerSock = (SSLServerSocket) serverSocketFactory.createServerSocket(NETWORK_PORT);
            ServerSock.setEnabledCipherSuites(ServerSock.getSupportedCipherSuites());
            ServerSock.setNeedClientAuth(true);
            
            //ServerSock.setNeedClientAuth(true);
            
            printServerInformation();
        
        }catch (IOException IOE) { IOE.printStackTrace();  }
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
    
    private void printServerInformation() {
        System.out.println("Allows SSL Sockets: " + this.ServerSock.getEnableSessionCreation());
        System.out.println("Use client mode: "+this.ServerSock.getUseClientMode());
        System.out.println("Need authentication: "+this.ServerSock.getNeedClientAuth());
        System.out.println("Want authentication: "+this.ServerSock.getWantClientAuth());
    }
    
}