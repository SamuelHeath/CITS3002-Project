package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

import net.Message;

/**
 *
 * @author Samuel Heath
 */
public class Server {
    
    private static int NETWORK_PORT; // Port on which the server listens
    
    
    public Server(int NET_PORT) {
        NETWORK_PORT = NET_PORT;
        System.out.println("Starting Server on port: " + NET_PORT);
        
        try {
            ServerSocketFactory ServerSockFactory = (ServerSocketFactory) ServerSocketFactory.getDefault();
            ServerSocket ServerSock = (ServerSocket) ServerSockFactory.createServerSocket(NETWORK_PORT);
            Socket clientSock = (Socket) ServerSock.accept();

            InputStream inputstream = clientSock.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            
            OutputStream outputstream = clientSock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(outputstream, true);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                Message m = new Message(string);
                System.out.println(m.getRawData());
                pwrite.println(m.toString());
                pwrite.flush();
            }
        
        } catch (IOException IOE) { IOE.printStackTrace(); }
        
        //run();
    }
    /**
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
        
    }**/
    
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