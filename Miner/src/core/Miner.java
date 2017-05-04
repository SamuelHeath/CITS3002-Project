package core;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Sam
 */
public class Miner {

    private static InetAddress NETWORK_IP;
    private static int NETWORK_PORT;

    /**
     * Network refers to the 'network' made up of interconnecting computers in
     * this project.
     *
     * @param netIP Network IP Address
     * @param netPort Network Port
     */
    public Miner(InetAddress netIP, int netPort) {
        NETWORK_IP = netIP;
        NETWORK_PORT = netPort;
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return NETWORK_IP.getHostName() + " " + String.valueOf(NETWORK_PORT);
    }

    /**
     * Use SSLSocket to connect to a server which is running an SSLServerSocket.
     */
    public void connectToNetwork() {

    }

    /**
     * Initiates doing a proof of work on a transaction. Just example not
     * actual code!
     *
     * @param message The message to be hashed.
     */
    public void proofOfWork(String message) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException NSAE) {
            NSAE.printStackTrace();
        }
        byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        //Check the first byte and see what the value is.
        System.out.println(hash[0]);
    }

    public static boolean checkAddress(InetAddress address) {
        // Timeout in msec.
        short timeout = 200;
        try {
            address.isReachable(timeout);
            return true;
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return false;
    }

}
