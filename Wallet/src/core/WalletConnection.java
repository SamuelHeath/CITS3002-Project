package core;

import static core.Wallet.balance;
import java.io.*;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;

//"-Djavax.net.ssl.trustStore=bin/Analysts/publicKey.jks"

/**
 * Creates a connection with the miner and then passes information to the Wallet.
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
 */
public class WalletConnection {
    
    private static PrintWriter pwrite;
    private static Wallet w;
    
    public static void main(String[] args) {
        //PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "123456".toCharArray());
        if (args.length >= 3) {
            
            if (!args[2].equals("--update") && !args[2].equals("--tx")) {
                System.out.println("Error");
                System.exit(1);
            }
            
            try {
                System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","password");
		System.setProperty("javax.net.ssl.trustStore", "wallet.jks");
                
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                
                sslsocket.setNeedClientAuth(true);
                sslsocket.startHandshake();
                System.out.println("\nConnected to Miner");
                
                System.out.println("Need client authentication: "+sslsocket.getNeedClientAuth());
		SSLSession sslsesh = sslsocket.getSession();
		System.out.println("Cipher suite: "+sslsesh.getCipherSuite());
		System.out.println("Protocol: "+sslsesh.getProtocol() + "\n");
                
                InputStream serverstream = sslsocket.getInputStream();
                InputStreamReader serverstreamreader = new InputStreamReader(serverstream);
                BufferedReader bufferedserverreader = new BufferedReader(serverstreamreader);
                
                OutputStream outputstream = sslsocket.getOutputStream();
                pwrite = new PrintWriter(outputstream, true);
                
                KeyPairGen.readKeys(); //Required!
                System.out.println("Chriscoin Address: " + KeyPairGen.getPublicKeyAddress() + "\n");
                
                w = new Wallet(args);
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
                            System.out.println("\nBalance: "+String.format("%.8f", balance));
                            Wallet.setResponse(true);
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
            w.getIO().readBlockChainFromStream(m.getRawData());
            Wallet.setResponse(true);
            System.out.println("\nBalance: "+String.format("%.8f", w.balance));
        } else {
            w.getIO().readBlocksFromStream(m.getRawData());
            Wallet.setResponse(true);
            System.out.println("\nBalance: "+String.format("%.8f", w.balance));
        }
        
    }
    
    public static void sendMessage(Message m) {
        pwrite.println(m.toString());
        pwrite.flush();
    }
    
}