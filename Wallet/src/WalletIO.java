
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
    
    /**
     * Read BlockChain from file and put it into the blockchain arraylist
     */
    public void readBlockChain() {
    
    }
    
    /**
     * Creates Block from some raw input text -- from Miner
     * @param inputText
     * @return 
     */
    public static Block constructBlock(String inputText) {
        Transaction[] transactions = new Transaction[2];
        return new Block("","",0,0,0,transactions);
    }
    
}