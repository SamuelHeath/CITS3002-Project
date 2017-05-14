package core;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.Message;

/**
 *
 * @author Sam
 */
public class Miner implements Runnable {

    private static int proof_difficulty = 3; //Default difficulty for miner
    
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
            proof_difficulty = difficulty;
        } else proof_difficulty = difficulty;
    }
    
    @Override
    public void run() {
        System.out.println("Miner Difficulty: " + proof_difficulty);
    }
      
    /**
     * Performs the proof of work on some input message.
     * @param message           The message to be hashed.
     * @return                  
     */
    public static String proofOfWork(String message) {
        long init_time = System.currentTimeMillis();
        int nonce = 0;
        int time_stamp = (int) (System.currentTimeMillis() / 1000L);
        
        byte[] byteNonce = genByteArrFromInt(nonce);
        byte[] byteMsg = message.getBytes(StandardCharsets.US_ASCII);
        byte[] comb = concatByteArr(byteMsg,byteNonce,time_stamp);
        try {
            
            
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = hasher.digest(hasher.digest(comb));
            System.out.print("Start Hash: ");
            System.out.println(Base58Check.encode(encodedhash));
            System.out.println("---------------------------------------");
            long init_time2 = System.currentTimeMillis();
            long numHashes = 1;
            while (!checkHashedBits(encodedhash)) {
                numHashes++;
                if (System.currentTimeMillis()-init_time2 > 30000) {
                    System.out.println("Hashes/sec "+numHashes/30);
                    numHashes=0;
                    init_time2 = System.currentTimeMillis();
                }
                if (Integer.MAX_VALUE == nonce) {
                    //Add Timestamp & start nonce over again
                    time_stamp = (int) (System.currentTimeMillis() / 1000L);
                    nonce = 0;
                }
                byteMsg = message.getBytes(StandardCharsets.US_ASCII);
                byteNonce = genByteArrFromInt(++nonce);
                comb = concatByteArr(byteMsg,byteNonce,time_stamp);
                hasher.update(comb);
                encodedhash = hasher.digest();
            }
            message = Base58Check.encode(encodedhash);
            System.out.println("End Hash:   " + message);
            System.out.println("Time: " + (float)(System.currentTimeMillis() - init_time)/60000 + "min " + "Nonce: " + nonce);
        } catch (NoSuchAlgorithmException NSAE) {}
        
        return message+"--"+nonce+"--"+time_stamp;
    }
    
    /**
     * Checks to see if the hasher has set the first n bytes to 0, where n is
     * equal to the difficulty factor and less than 32 (256bit -> 32 bytes).
     * @param hash              The SHA-256 hashed message.
     * @return                  Return true if the hasher hasn't been met.
     */
    public static boolean checkHashedBits(byte[] hash) {
        for (int i = 0; i < proof_difficulty; i++) {
            if (hash[i] != 0) return false;
        }
        return true;
    }
    
    /**
     * @return                  A 4 byte array randomly generated from an int.
     */
    private static byte[] genByteArrFromInt(int current_nonce) {
        return ByteBuffer.allocate(4).putInt(current_nonce).array();
    }
    
    /**
     * @param message           The message in byte array.
     * @param nonce             The nonce in byte array.
     * @param time_stamp
     * @return                  Whether or not the proof of work is valid.
     */
    public static boolean checkProofOfWork(byte[] message, byte[] nonce, int time_stamp) {
        
        byte[] combined = concatByteArr(concatByteArr(message,nonce),genByteArrFromInt(time_stamp));
        try {
            MessageDigest hashd = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = hashd.digest(hashd.digest(combined));
            for (int i=0; i < proof_difficulty; i++) {
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
    private static byte[] concatByteArr(byte[] msg, byte[] nonce) {
        byte[] concatArr = new byte[msg.length+nonce.length];
        System.arraycopy(msg, 0, concatArr, 0, msg.length);
        System.arraycopy(nonce, 0, concatArr, msg.length, nonce.length);
        return concatArr;
    }
    
    /**
     * Concatinates the nonce byte array with the message byte array, allowing
     * SHA-256 to work on the whole array.
     * @param msg                   The msg byte array.
     * @param nonce                 The nonce byte array to be appended.
     * @return                      The resulting byte array after concatination.
     */
    private static byte[] concatByteArr(byte[] msg, byte[] nonce, int time_stamp) {
        byte[] time = genByteArrFromInt(time_stamp);
        byte[] concatArr = new byte[msg.length+nonce.length+time.length];
        System.arraycopy(msg, 0, concatArr, 0, msg.length);
        System.arraycopy(nonce, 0, concatArr, msg.length, nonce.length);
        System.arraycopy(time, 0, concatArr, msg.length+nonce.length, time.length);
        return concatArr;
    }
    
    public static void transactionMessage(Message transaction) {
        String hashed_transaction = proofOfWork(transaction.getRawData());
        Server.broadcastMessage(new Message("BCST:"+hashed_transaction));
    }
    
    /**
     * @return                  The current longest Blockchain of the system.
     */
    public static Message blockChainRequested() {
        return new Message("REQRS:this is le block chain");
    }
    
}