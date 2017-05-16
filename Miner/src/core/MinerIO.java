/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.Random;

/**
 *
 * @author user
 */
class MinerIO {
    
    public static String getCoinBaseAddress() {
        return generateAddress();
    }
    
    public static String getLastHash() {
        return generateAddress();
    }
    
    private static String generateAddress() {
        String s = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        Random rchar = new Random();
        String transaction = "";
        for (int i = 0; i < 128; i++) {
            transaction = transaction + String.valueOf(s.charAt(rchar.nextInt(s.length())));
        }
        return transaction;
    }
    
}
