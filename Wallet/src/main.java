import java.io.*;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Random;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;

import net.Message;

//"-Djavax.net.ssl.trustStore=bin/Analysts/publicKey.jks"

/**
 * @author Samuel Heath
 */
public class main {
    
    public static void main(String[] args) {
        //PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "123456".toCharArray());
        if (args.length >= 3) {
            try {
                String current_dir = System.getProperty("user.dir") + "/cacerts.jks";
                System.out.println(current_dir);
                System.setProperty("javax.net.ssl.trustStore", current_dir);
                System.setProperty("javax.net.ssl.trustStorePassword", "123456");
                
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                
                sslsocket.startHandshake();
                System.out.println("Connected to Network");
                
                System.out.println("Need client authentication = "+sslsocket.getNeedClientAuth());
		SSLSession sslsesh = sslsocket.getSession();
		System.out.println("Cipher suite = "+sslsesh.getCipherSuite());
		System.out.println("Protocol = "+sslsesh.getProtocol());
                
                InputStream cmdstream = System.in;
                InputStreamReader cmdstreamreader = new InputStreamReader(cmdstream);
                BufferedReader bufferedcmdreader = new BufferedReader(cmdstreamreader);
                
                InputStream serverstream = sslsocket.getInputStream();
                InputStreamReader serverstreamreader = new InputStreamReader(serverstream);
                BufferedReader bufferedserverreader = new BufferedReader(serverstreamreader);
                
                OutputStream outputstream = sslsocket.getOutputStream();
                PrintWriter pwrite = new PrintWriter(outputstream, true);
                
                WalletIO wio = new WalletIO();
                
                /****
                 * IMPORTANT
                 */
                KeyPairGen.generateKeys(); //Required!
                System.out.println("Public Key: " + KeyPairGen.getPublicKeyAddress());
                
                if (args[2].equals("--trans")) {
                    //Get latest hash off the stored bitcoin ledger, and add to below call.
                    pwrite.println("REQBC;H");
                    pwrite.flush();
                    String[] input = args[3].split(" ");
                    if (input.length == 2) {
                        Transaction t = new Transaction(KeyPairGen.getPublicKeyAddress(),input[0],Double.valueOf(input[1]));
                        t.signTransaction();
                        pwrite.println("TRNS;"+t.transactionToString());
                        pwrite.flush();
                    } else { System.out.println("Unknown Command."); System.exit(-1); }
                } else if (args[2].equals("--update")) {
                    System.out.println("");
                    pwrite.println("REQBC;H");
                    pwrite.flush();
                } else {
                    System.out.println("You must enter atleast 3 arguments");
                    System.exit(-1);
                }
                
                String server_resp = null;
                while ((server_resp = bufferedserverreader.readLine()) != null) {
                    Message m = new Message(server_resp);
                    switch (m.getType()) {
                        case "BCRS":
                            updateWallet(m);
                            break;
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
    
    private static void updateWallet(Message m) {
        WalletIO.readBlockChain(m.getRawData());
        System.out.println(WalletIO.getBalance());
    }
    
    
    private static byte[] sign(byte[] transaction, PrivateKey private_key) {
		try {
                    Signature s = Signature.getInstance("SHA256withRSA");
		    s.initSign(private_key);
		    s.update(transaction);
		    byte[] signature = s.sign();
		    
                    return signature;
		} catch (NoSuchAlgorithmException NSAE) {
			NSAE.printStackTrace();
			
		} catch (InvalidKeyException IKE) { IKE.printStackTrace(); 
                } catch (SignatureException SE) { SE.printStackTrace(); }
	return null;	
    }
    
    public static String genTrnsStr() {
        String s = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        Random rchar = new Random();
        String transaction = "";
        for (int i = 0; i < 64; i++) {
            transaction = transaction + String.valueOf(s.charAt(rchar.nextInt(s.length())));
        }
        return transaction;
    }
}