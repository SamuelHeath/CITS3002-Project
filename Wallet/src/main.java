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
                String current_dir = System.getProperty("user.dir") + "\\cacerts.jks";
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
                
               
                if (args[2].equals("--trans")) {
                    //Get latest hash off the stored bitcoin ledger, and add to below call.
                    pwrite.println("REQBC:H");
                    pwrite.flush();
                    String[] input = args[3].split(" ");
                    if (input.length == 3) {
                        /*sign(transaction2Bytes(input[0].getBytes(StandardCharsets.US_ASCII),
                                input[1].getBytes(StandardCharsets.US_ASCII),
                                ByteBuffer.allocate(4).putFloat(Float.valueOf(input[2])).array()),
                                );*/
                        pwrite.println("TRNS:"+genTrnsStr()+"--"+genTrnsStr()+"--"+0.5+"--"+genTrnsStr());
                        pwrite.flush();
                    } else { System.out.println("Unknown Command."); System.exit(-1); }
                } else if (args[2].equals("--update")) {
                    System.out.println("");
                    pwrite.println("REQBC:H");
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
                            System.out.println("Block-Chain Recieved");
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
        System.out.println("You have 25 chriscoin.");
    }
    
    
    private static byte[] transaction2Bytes(byte[] sender_key, byte[] receiver_key, byte[] amount) {
        return concatByteArr(concatByteArr(sender_key,receiver_key),amount);
    }
    
    /**
     * @param a                     The first byte array.
     * @param b                     The second byte array to be appended.
     * @return                      The resulting byte array after concatination.
     */
    private static byte[] concatByteArr(byte[] a, byte[] b) {
        byte[] concatArr = new byte[a.length+b.length];
        System.arraycopy(a, 0, concatArr, 0, b.length);
        System.arraycopy(b, 0, concatArr, a.length, b.length);
        return concatArr;
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