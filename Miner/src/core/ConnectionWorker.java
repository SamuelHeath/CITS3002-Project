package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author Samuel Heath
 */
public class ConnectionWorker implements Runnable {
    
    private final SSLSocket clientSock;
    private PrintWriter pwrite;
    
    public ConnectionWorker(SSLSocket clientSocket) {
        this.clientSock = clientSocket;
    }
    
    @Override
    public void run() {
        try {
            clientSock.startHandshake();
            SSLSession sslSession = clientSock.getSession();
            System.out.println(sslSession.getProtocol());
            
            InputStream inputstream = clientSock.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            OutputStream outputstream = clientSock.getOutputStream();
            pwrite = new PrintWriter(outputstream, true);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                    Message m = new Message(string);
                    switch (m.getType()) {
                        case "RQBC":
                                pwrite.println(Miner.blockChainRequested(m));
                                pwrite.flush();
                            break;
                        case "BCST":
                            Server.broadcastMessage(m);
                            break;
                        case "TX":
                            Miner.transactionMessage(m);
                            break;
                        default:
                            pwrite.println(m.toString());
                            pwrite.flush();
                            break;
                    }
            }
        } catch (IOException IOE) {
            Server.getConnections().remove(this.clientSock);
        }
    }
    
    public void closeSocket() {
        try {
            this.clientSock.close();
        } catch (IOException IOE) {}
    }
    
    /**
     * Sends a message on the connected socket.
     * @msg                 Holds the message being passed to this socket.
     */
    public void sendMessage(Message msg) {
        this.pwrite.println(msg.toString());
        this.pwrite.flush();
    }
    
}
