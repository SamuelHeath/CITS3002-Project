package core;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


import net.Message;

/**
 *
 * @author Sam
 */
public class Miner implements Runnable {

    private int proof_difficulty = 3; //Default difficulty for miner
    
    /**
     * Default initialisation of the Miner.
     */
    public Miner() {
        
    }
    
    /**
     * Initialises the Miner object so that it has a set difficulty
     * @param difficulty        The number of 0's required at the front of a hashed message.
     */
    public Miner(int difficulty) {
        if (difficulty > 32) {
            this.proof_difficulty = difficulty;
        } else this.proof_difficulty = difficulty;
    }
    
    @Override
    public void run() {
        System.out.println("Miner Difficulty: " + this.proof_difficulty);
        
        while (true) {
            proofOfWork("string");
        }
    }
      
    /**
     * Performs the proof of work on some input message.
     * @param message           The message to be hashed.
     */
    public void proofOfWork(String message) {
        long init_time = System.currentTimeMillis();
        message = "Akjs89djhfioHA35jhfiwufhuidjh24w543hfiw878u87983ubIUBSI5235UBsvbwikvbwuevIUWbi78brwkVBebvKEB";
        byte[] byteNonce = generateNonce();
        byte[] byteMsg = message.getBytes(StandardCharsets.UTF_8);
        byte[] comb = concatNonce(byteMsg,byteNonce);
        byte firstByte = 2;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(comb);
            System.out.print("Start Hash: ");
            for (int i = 0; i < encodedhash.length; i++) {System.out.print(encodedhash[i] + " ");}
            System.out.println("\n---------------------------------------");
            long init_time2 = System.currentTimeMillis();
            long numHashes = 1;
            while (!checkHash(encodedhash)) {
                numHashes++;
                if (System.currentTimeMillis()-init_time2 > 30000) {
                    System.out.println("Hashes/sec "+numHashes/30);
                    numHashes=0;
                    init_time2 = System.currentTimeMillis();
                }
                
                byteMsg = message.getBytes(StandardCharsets.UTF_8);
                byteNonce = generateNonce();
                comb = concatNonce(byteMsg,byteNonce);
                encodedhash = digest.digest(comb);
            }
            firstByte = encodedhash[0];
            System.out.print("End Hash: ");
            for (int i = 0; i < encodedhash.length; i++) {System.out.print(encodedhash[i] + " ");}
            System.out.println("\n");
        } catch (NoSuchAlgorithmException NSAE) {}
        
        System.out.println("Time: " + (float)(System.currentTimeMillis() - init_time)/60000 + "min " + "Nonce: " + nonceToString(byteNonce));
        
        if (checkProofOfWork(byteMsg, byteNonce)) System.out.println("True");
    }
    
    /**
     * Checks to see if the hash has set the first n bytes to 0, where n is
     * equal to the difficulty factor and less than 32 (256bit -> 32 bytes).
     * @param hash              The SHA-256 hashed message.
     * @return                  Return true if the hash hasn't been met.
     */
    public boolean checkHash(byte[] hash) {
        for (int i = 0; i < this.proof_difficulty; i++) {
            if (hash[i] != 0) return false;
        }
        return true;
    }
    
    /**
     * @param nonce             The nonce as a byte array.
     * @return                  The nonce represented as a string, based on byte representation of characters.
     */
    public String nonceToString(byte[] nonce) {
        try {
            String nonceStr =  new String(nonce, "UTF-8");
            return nonceStr;
        } catch (UnsupportedEncodingException UEE) {}
        return "";
    }
    
    /**
     * @return                  A 4 byte array randomly generated from keyboard characters.
     */
    public byte[] generateNonce() {
        String alphabet = "!@#$%^&*()-_=+[{]}|<>:;.,?/'~0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        Random r = new Random();
        byte[] nonce = new byte[4];
        for (int i = 0; i < 4; i++) {
            //Convert char - a byte representing ASCII - to a byte
            nonce[i] = (byte)alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return nonce;
    }
    
    /**
     * @param message           The message in byte array.
     * @param nonce             The nonce in byte array.
     * @return                  Whether or not the proof of work is valid.
     */
    public boolean checkProofOfWork(byte[] message, byte[] nonce) {
        
        byte[] combined = concatNonce(message,nonce);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(combined);
            for (int i=0; i < this.proof_difficulty; i++) {
                if (encodedhash[i] != 0) return false;
            }
        } catch (NoSuchAlgorithmException NSAE) {}
        return true;
    }
    
    /**
     * Concatinates the nonce byte array with the message byte array, allowing
     * SHA-256 to work on the whole array.
     * @param msg                   The msg byte array.
     * @param nonce                 The nonce byte array to be appended.
     * @return                      The resulting byte array after concatination.
     */
    public byte[] concatNonce(byte[] msg, byte[] nonce) {
        byte[] concatArr = new byte[msg.length+nonce.length];
        System.arraycopy(msg, 0, concatArr, 0, msg.length);
        System.arraycopy(nonce, 0, concatArr, msg.length, nonce.length);
        return concatArr;
    }
    
    /**
     * @return                  The current longest Blockchain of the system.
     */
    public static Message blockChainRequested() {
        return new Message("REQRS:this is le block chain");
    }
    
}