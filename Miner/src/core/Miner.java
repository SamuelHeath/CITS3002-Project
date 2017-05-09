package core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Sam
 */
public class Miner {

    /**
     * 
     */
    public Miner() {
    }
    
    @Override
    public String toString() {
        return "";
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
}