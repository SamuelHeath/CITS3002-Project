
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Handles the IO to what ever file it has.
 * @author Samuel James Serwan Heath
 */
public class WalletIO implements Runnable {
    
    private static BlockChain block_chain = new BlockChain();
    private static final String CHAIN_FILENAME = "block-chain.dat";
    
    public WalletIO() {
        File f = new File(CHAIN_FILENAME);
        if (f.exists()) {
            try {
                //Reads serialized blockchain stored on computer
                System.out.println("Reading Block Chain");
                block_chain = readBlockChain(f);
                System.out.println("Size: " + block_chain.getBlocks().size());
            } catch (FileNotFoundException FNFE) {}
        }
    }
    @Override
    public void run() {
        
    }
    
    
    public static double getBalance() {
        double balance = 0.0;
        
        String pubKey = KeyPairGen.getPublicKeyAddress();
        for (Block b:block_chain.getBlocks()) {
            for (Transaction t:b.getTransactions()) {
                if (t.getSenderKey().equals(pubKey)) {
                    //Subtract from balance
                    balance -= t.getTransactionAmount();
                    continue;
                } else if (t.getReceiverKey().equals(pubKey)) {
                    //Add
                    balance += t.getTransactionAmount();
                }
            }
        }
    }
    
    /**
     * Read BlockChain from file and put it into the blockchain arraylist
     */
    private BlockChain readBlockChain(File f) throws FileNotFoundException {
        BlockChain bc = null;
        try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
            bc = (BlockChain) ois.readObject();
            return bc;
        } catch (IOException IOE) { IOE.printStackTrace(); 
        } catch (ClassNotFoundException CNFE) {CNFE.printStackTrace();}
        return bc;
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