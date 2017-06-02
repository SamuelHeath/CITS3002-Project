import com.google.gson.Gson;
import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;

import net.Message;

//"-Djavax.net.ssl.trustStore=bin/Analysts/publicKey.jks"

/**
 * Creates a connection with the miner and then passes information to the Wallet.
 * @author Samuel Heath
 */
public class WalletConnection {
    
    private static PrintWriter pwrite;
    
    public static void main(String[] args) {
        //PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "123456".toCharArray());
        if (args.length >= 3) {
            
            if (!args[2].equals("--update") && !args[2].equals("--tx")) {
                System.out.println("Error");
                System.exit(1);
            }
            
            try {
                System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","alicepassword");
		System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
                
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                
                sslsocket.startHandshake();
                System.out.println("Connected to Network");
                
                System.out.println("Need client authentication = "+sslsocket.getNeedClientAuth());
		SSLSession sslsesh = sslsocket.getSession();
		System.out.println("Cipher suite = "+sslsesh.getCipherSuite());
		System.out.println("Protocol = "+sslsesh.getProtocol() + "\n");
                
                InputStream cmdstream = System.in;
                InputStreamReader cmdstreamreader = new InputStreamReader(cmdstream);
                BufferedReader bufferedcmdreader = new BufferedReader(cmdstreamreader);
                
                InputStream serverstream = sslsocket.getInputStream();
                InputStreamReader serverstreamreader = new InputStreamReader(serverstream);
                BufferedReader bufferedserverreader = new BufferedReader(serverstreamreader);
                
                OutputStream outputstream = sslsocket.getOutputStream();
                pwrite = new PrintWriter(outputstream, true);
                
                KeyPairGen.readKeys(); //Required!
                System.out.println("Public Key: " + KeyPairGen.getPublicKeyAddress() + "\n");
                
                WalletIO wio = new WalletIO();
                new Thread(wio).start();
                
                Wallet w = new Wallet(args);
                new Thread(w).start();
                
                String server_resp = null;
                while ((server_resp = bufferedserverreader.readLine()) != null) {
                    Message m = new Message(server_resp);
                    switch (m.getType()) {
                        // Block Chain Response
                        case "BCRS":
                            updateWallet(m);
                            break;
                        // Block Response
                        case "BKRS":
                            updateWallet(m);
                            break;
                        case "NBKN":
                            System.out.println("I'm up to date yay :)");
                            //If the miner says there is nothing needed to change,
                            //then break the while loop and just chill out.
                            Wallet.responseOccured(true);
                        default:
                            System.out.println(m.getRawData());
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("You must enter atleast 3 arguments");
            System.exit(-1);
        }
    }
    
    /**
     * Updates the Blockchain stored with the wallet.
     * @param m                     The message which contains a block chain.
     */
    private static void updateWallet(Message m) {
        if (m.getType().equals("BCRS")) {
            WalletIO.readBlockChainFromStream(m.getRawData());
            System.out.println("\nBalance: "+WalletIO.getBalance() + "\n");
        } else {
            WalletIO.readBlocksFromStream(m.getRawData());
            System.out.println("\nBalance: "+Wallet.balance);
        }
        
    }
    
    public static void sendMessage(Message m) {
        pwrite.println(m.toString());
        pwrite.flush();
    }
    
}