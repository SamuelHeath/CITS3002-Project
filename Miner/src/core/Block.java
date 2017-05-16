/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author Samuel Heath
 */
public class Block {
    
    private String block_hash;
    private final Transaction[] transactions;
    private final String prevHash;
    private String cBase;
    private final String merkRoot;
    private int time_stamp;
    private int block_nonce;
    private final int block_transCount;
    
    public Block(String previousHash, String coinBase, int timeStamp, int nonce, int transCount) {
        this.transactions = new Transaction[transCount];
        this.prevHash = previousHash;
        this.cBase = coinBase;
        this.time_stamp = timeStamp;
        this.block_nonce = nonce;
        this.block_transCount = transCount;
        this.merkRoot = calcMerkelRoot();
    }
    
    private String calcMerkelRoot() {
        return "";
    }
    
    public String getMerkelRoot() {
        return this.merkRoot;
    }
    
    public String getPreviousHash() {
        return this.prevHash;
    }

    public String getCoinBase() {
        return this.cBase;
    }
    
    public int getTimeStamp() {
        return this.time_stamp;
    }
    
    public void setTimeStamp(int new_time) {
        this.time_stamp = new_time;
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
        sb.append(this.prevHash);
        sb.append("--");
        sb.append(this.merkRoot);
        sb.append("--");
        sb.append(this.time_stamp);
        sb.append("--");
        sb.append(this.block_transCount);
        sb.append("--");
        sb.append(this.block_nonce);
        for (Transaction t : transactions) {
            sb.append("--");
            sb.append(t.toString());
        }
        return sb.toString();
    }
    
    public void setHash(String hash) {
        this.block_hash = hash;
    }
    
    public String getHash() {
        return this.block_hash;
    }
    
}