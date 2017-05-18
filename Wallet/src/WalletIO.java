
import java.util.ArrayList;

/**
 * Handles the IO to what ever file it has.
 * @author Samuel Heath
 */
public class WalletIO implements Runnable {
    
    private final ArrayList<Block> blockchain = new ArrayList(2);
    
    @Override
    public void run() {
        
    }
    
    public void updateWallet() {
        readBlockChain(); //
    }
    
    /**
     * Read BlockChain from file and put it into the blockchain arraylist
     */
    private void readBlockChain() {
    
    }
    
    private static Transaction[] processTransaction(String[] raw_trans, int num_trans) {
        Transaction[] tx = new Transaction[num_trans];
        for (int i =0; i < num_trans; i++) {
            String[] tx_comps = raw_trans[i].replace("'", "").split("-");
            tx[i] = new Transaction(tx_comps[0],tx_comps[1],Double.valueOf(tx_comps[2]),tx_comps[3]);
        }
        return tx;
    }
    
    /**
     * Creates Block from some raw input text -- from Miner
     * @param inputText
     * @return 
     */
    public static Block constructBlock(String inputText) {
        String[] parts = inputText.split("--");
        Transaction[] transactions = processTransaction(parts, 2);
        return new Block(parts[1],parts[2],Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4]),Integer.parseInt(parts[5]),transactions);
    }
    
}