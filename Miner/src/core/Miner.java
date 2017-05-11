package core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;


import net.Message;

/**
 *
 * @author Sam
 */
public class Miner implements Runnable {

    /**
     * 
     */
    public Miner() {
    }
    
    @Override
    public void run() {
        long init_time = System.currentTimeMillis();
        int nonce = 0;
        String message = "Akjs89djhfioHA35jhfiwufhuiw543hfiwuvcwubIUBSI5235UBsvbwikvbwuevIUWbi78brwkVBebvKEB";
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(nonce);
        byte[] byteNonce = bb.array();
        byte[] byteMsg = message.getBytes(StandardCharsets.UTF_8);
        byte[] comb = concatNonce(byteMsg,byteNonce);
        byte firstByte = 2;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(comb);
            for (int i = 0; i < encodedhash.length; i++) {System.out.println(encodedhash[i]);}
            System.out.println("---------------------------------------");
            long init_time2 = System.currentTimeMillis();
            
            while (!checkHash(encodedhash)) {
                if (System.currentTimeMillis()-init_time2 > 20000) {
                    System.out.println("Hash of first byte: "+encodedhash[0]);
                    System.out.println("Current Nonce: "+nonce);
                    init_time2 = System.currentTimeMillis();
                }
                nonce++;
                
                byteMsg = message.getBytes(StandardCharsets.UTF_8);
                bb.clear();
                bb.putInt(nonce);
                byteNonce = bb.array();
                comb = concatNonce(byteMsg,byteNonce);
                encodedhash = digest.digest(comb);
            }
            firstByte = encodedhash[0];
            for (int i = 0; i < encodedhash.length; i++) {System.out.println(encodedhash[i]);}
        } catch (NoSuchAlgorithmException NSAE) {}
        System.out.println("Nonce: " + nonce + " Byte: " + firstByte);
        
        System.out.println("Time: " + (System.currentTimeMillis() - init_time) + "ms");
        
        //if (checkProofOfWork(byteMsg, nonce)) System.out.println("True");
    }
        
    public boolean checkHash(byte[] hash) {
        for (int i = 0; i <= 1; i++) {
            if (hash[i] != 0 || hash[hash.length-1-i] != 0) return false;
        }
        return true;
    }
    
    public boolean checkProofOfWork(byte[] message, int nonce) {
        
        byte[] combined = null ;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] encodedhash = digest.digest(combined);
            if (encodedhash[0] == 0) { return true; }
        } catch (NoSuchAlgorithmException NSAE) {}
        return false;
    }
    
    public byte[] concatNonce(byte[] msg, byte[] nonce) {
        byte[] concatArr = new byte[msg.length+nonce.length];
        System.arraycopy(msg, 0, concatArr, 0, msg.length);
        System.arraycopy(nonce, 0, concatArr, msg.length, nonce.length);
        return concatArr;
    }
    
    public static Message blockChainRequested() {
        return new Message("REQRS:this is le block chain");
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