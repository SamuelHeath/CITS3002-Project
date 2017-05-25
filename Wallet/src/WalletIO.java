
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        
        String pubKey = Base58Check.encode(KeyPairGen.getPublicKey().getEncoded(),false);
        String pubKeyRec = KeyPairGen.getPublicKeyAddress(); //The easily bitcoin address
        for (Block b:block_chain.getBlocks()) {
            for (Transaction t:b.getTransactions()) {
                if (t.getSenderKey().equals(pubKey)) {
                    //Subtract from balance
                    balance -= t.getTransactionAmount();
                    continue;
                } else if (t.getReceiverKey().equals(pubKeyRec)) {
                    //Add
                    balance += t.getTransactionAmount();
                } else if (t.getReceiverKey().equals("0000")) {
                    balance += t.getTransactionAmount();
                }
            }
        }
        return balance;
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
    
    public static void readBlockChainFromStream(String s) {
        Gson g = new Gson();
        BlockChain bc = new BlockChain();
        block_chain = g.fromJson(s, bc.getClass());
        System.out.println("Latest BLock Hash: "+block_chain.getLastHash());
    }
}