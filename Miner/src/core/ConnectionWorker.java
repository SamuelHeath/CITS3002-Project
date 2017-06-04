package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
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
            clientSock.setNeedClientAuth(true);
            clientSock.startHandshake();
            SSLSession sslSession = clientSock.getSession();
            System.out.println("\nWallet Connected With: "+sslSession.getProtocol());
            
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
                            Message response = Miner.blockChainRequested(m);
                            pwrite.println(response.toString());
                            if (response.getType().equals("NBKN")) {
                                System.out.println("That wallet is up to date :)");
                            }
                            //Will always need to flush the writer.
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
