/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import net.Message;

/**
 *
 * @author user
 */
public class ConnectionWorker implements Runnable {
    
    private final Socket clientSock;
    private PrintWriter pwrite;
    
    public ConnectionWorker(Socket clientSocket) {
        this.clientSock = clientSocket;
    }
    
    
    @Override
    public void run() {
        try {
            InputStream inputstream = clientSock.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            OutputStream outputstream = clientSock.getOutputStream();
            pwrite = new PrintWriter(outputstream, true);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                    Message m = new Message(string);
                    switch (m.getType()) {
                        case "REQBC":
                            pwrite.println(Miner.blockChainRequested());
                            pwrite.flush();
                            break;
                        case "BCST":
                            Server.broadcastMessage(m);
                            break;
                        default:
                            System.out.println(m.getRawData());
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
