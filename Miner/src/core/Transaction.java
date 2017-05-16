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
public class Transaction {
    
    private final String sender_key;
    private final String reciever_key;
    private final float coin_amount;
    private String signature;
    
    public Transaction(String senderKey, String recieverKey, float chrisCoins) {
        this.sender_key = senderKey;
        this.reciever_key = recieverKey;
        this.coin_amount = chrisCoins;
    }
    
    public Transaction(String senderKey, String recieverKey, float chrisCoins, String signature) {
        this.sender_key = senderKey;
        this.reciever_key = recieverKey;
        this.coin_amount = chrisCoins;
    }
    
    /**
     * @return                      Whether or not the transaction signature is valid.
     */
    public boolean verifySignature() {
        System.out.println("Verified!");
        return true;
    }
    
    
    public String signCoinBaseTransaction(String KeySign) {
        if (this.signature == null) { return ""; }
        return "";
    }
    
}