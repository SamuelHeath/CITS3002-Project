
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.xml.bind.DatatypeConverter;

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
                Wallet.balance = getBalance();
                System.out.println("Size: " + block_chain.getBlocks().size());
            } catch (FileNotFoundException FNFE) {}
        } else {
            Wallet.balance = 0.0;
        }
    }
    @Override
    public void run() {
        
    }
    
    public static double updateBalance(Block[] b) {
        double bal = Wallet.balance;
        String pubKey = Base58Check.encode(KeyPairGen.getPublicKey().getEncoded(),false);
        String pubKeyRec = KeyPairGen.getPublicKeyAddress(); //The easily bitcoin address
        String zeroHex = DatatypeConverter.printHexBinary(new byte[32]);
        for (Block block: b) {
            for (Transaction t:block.getTransactions()) {
                if (t.getSenderKey().equals(pubKey)) {
                    bal -= t.getTransactionAmount();
                } else if (t.getReceiverKey().equals(pubKeyRec)) {
                    bal += t.getTransactionAmount();
                } else if (t.getReceiverKey().equals(zeroHex)) {
                    bal += t.getTransactionAmount();
                }
            }
        }
        Wallet.balance = bal;
        return bal;
    }
    
    public static double getBalance() {
        double balance = 0.0;
        
        String pubKey = Base58Check.encode(KeyPairGen.getPublicKey().getEncoded(),false);
        String pubKeyRec = KeyPairGen.getPublicKeyAddress(); //The easily bitcoin address
        String zeroHex = DatatypeConverter.printHexBinary(new byte[32]); // The free coins given to all wallets by the system.
        for (Block b:block_chain.getBlocks()) {
            for (Transaction t:b.getTransactions()) {
                if (t.getSenderKey().equals(pubKey)) {
                    //Subtract from balance
                    balance -= t.getTransactionAmount();
                    continue;
                } else if (t.getReceiverKey().equals(pubKeyRec)) {
                    //Add
                    balance += t.getTransactionAmount();
                } else if (t.getReceiverKey().equals(zeroHex)) {
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
    
    public static void readBlocksFromStream(String s) {
        Gson g = new Gson();
        Block[] b = g.fromJson(s, Block[].class);
        for (int i = b.length-1; i >= 0; i ++) {
            //adds from the bottom of the array to maintain order of the block chain.
            // As the miner will just send the array, added extra check to ensure it only adds blocks it doesnt already have.
            try {
                int blkNum = block_chain.getLatestBlockNumber();
                if (b[i].getBlockNumber() > blkNum) {
                    block_chain.addBlock(b[i]);
                }
            } catch (Exception E) {}
        }
    }
}