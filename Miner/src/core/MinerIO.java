package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Samuel Heath
 */
class MinerIO {
    
    private static ArrayList<Block> blockChain; // Stores the block chain.
    
    public MinerIO() {
        blockChain = new ArrayList(20);
    }
    
    /**
     * @return                  The whole block-chain if receiver doesn't have anything.
     */
    public static ArrayList<Block> getBlockChain() {
        return blockChain;
    }
    
    /**
     * @param last_hash         The last hash the user had stored.
     * @return                  All blocks on the block chain after the last hash.
     */
    public static ArrayList<Block> getBlockChain(String last_hash) {
        ArrayList<Block> chain = new ArrayList(1);
        boolean found = false;
        for (int i = blockChain.size()-1; i >= 0; i--) {
            if (blockChain.get(i).getHash().equals(last_hash)) {
                found = true;
            }
            if (found) {
                chain.add(0,blockChain.get(i));
            }
        }
        //Return null if chain is empty or the chain if false.
        return chain.isEmpty() ? null : chain;
    }
    
    /**
     * @return                  The Coin Base Address which stored somewhere.
     */
    public static String getCoinBaseAddress() {
        return generateAddress();
    }
    
    /**
     * @return                  The hash of the last block in the system.
     */
    public static String getLastHash() {
        //return blockChain.get(blockChain.size()-1).getHash();
        return generateAddress();
    }
    
    private static String generateAddress() {
        String s = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        Random rchar = new Random();
        StringBuilder transaction = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            transaction.append(String.valueOf(s.charAt(rchar.nextInt(s.length()))));
        }
        return transaction.toString();
    }
    
    /**
     * Reads the file in the directory and creates the block-chain as Block
     * objects in the blockChain variable.
     */
    public void readBlockChain() {
        try {
            File f = new File("block.chain");
            if (!f.exists()) {
                f.createNewFile();
                //Store Miner public key.
            }
            FileReader fr = new FileReader(f.getCanonicalFile());
            BufferedReader b = new BufferedReader(fr);
            
            //Close the stream
            b.close();
        } catch (FileNotFoundException FNFE) { FNFE.printStackTrace();
        } catch (IOException IOE) { IOE.printStackTrace(); }
    }
    
}
