package core;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Samuel Heath
 */
public class Block implements Serializable {
    
    private String block_hash;
    private String merkelRoot;
    private final Transaction[] block_transactions;
    private final String prev_hash;
    private final String coin_base;
    private int time_stamp;
    private int block_nonce;
    private int block_transCount;
    
    public Block(String previousHash, String coinBase, int timeStamp, int nonce, int transCount, Transaction[] transactions) {
        this.block_transactions = transactions;
        this.prev_hash = previousHash;
        this.coin_base = coinBase;
        this.time_stamp = timeStamp;
        this.block_nonce = nonce;
        this.block_transCount = transCount;
    }
    
    public String getPreviousHash() {
        return this.prev_hash;
    }

    public String getCoinBase() {
        return this.coin_base;
    }
    
    public int getTimeStamp() {
        return this.time_stamp;
    }
    
    public void setTimeStamp(int new_time) {
        this.time_stamp = new_time;
    }
    
    public int getTransactionCount() { return this.block_transCount; }
    
    public void setTransactionCount(int transaction_count) {
        this.block_transCount = transaction_count;
    }
    
    public int getNonce() {
        return this.block_nonce;
    }
    
    public void setNonce(int new_nonce) {
        this.block_nonce = new_nonce;
    }
    
    public String blockToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.block_hash);
        sb.append("--");
        sb.append(this.prev_hash);
        sb.append("--");
        sb.append(this.time_stamp);
        sb.append("--");
        sb.append(this.block_transCount);
        sb.append("--");
        sb.append(this.block_nonce);
        for (Transaction t : block_transactions) {
            sb.append("--");
            sb.append(t.transactionToString());
        }
        return sb.toString();
    }
    
    public void setHash(String hash) {
        this.block_hash = hash;
    }
    
    public String getHash() {
        return this.block_hash;
    }
    
    public byte[] getMerkelRoot() {
        return this.merkelRoot.getBytes(StandardCharsets.US_ASCII);
    }
    
    public void setMerkelRoot(String new_root) {
        this.merkelRoot = new_root;
    }
    
    public String merkel2String() {
        String s = "";
        try {
            s =Base58Check.encode(merkelRoot.getBytes(StandardCharsets.US_ASCII), false);
        } catch (NoSuchAlgorithmException NSAE) {}
        return s;
    }
    
    public Transaction[] getTransactions() { return this.block_transactions; }
    
}