import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.xml.bind.DatatypeConverter;
import net.Message;

/**
 * Handles the IO to what ever file it has.
 * @author Samuel James Serwan Heath
 */
public class WalletIO implements Runnable {
    
    private static BlockChain block_chain = new BlockChain();
    private static final String CHAIN_FILENAME = "block-chain.dat";
    private static final String JSON_CHAIN_FILE = "block-chain.json";
    
    public WalletIO() {
        File f = new File(CHAIN_FILENAME);
        if (f.exists()) {
            try {
                //Reads serialized blockchain stored on computer
                System.out.println("Reading Block Chain");
                block_chain = readBlockChain(f);
                Wallet.balance = getBalance();
                //Sends a request for all blocks higher than this number.
                WalletConnection.sendMessage(new Message("RQBC;"+block_chain.getLatestBlockNumber()));
                System.out.println("Size: " + block_chain.getBlocks().size());
            } catch (FileNotFoundException FNFE) {
            } catch (Exception E) {}
        } else {
            WalletConnection.sendMessage(new Message("RQBC;"));
            Wallet.balance = 0.0;
        }
    }
    
    @Override
    public void run() {
        //Just run it on another thread.
    }
    
    /**
     * Takes the result of an incoming BKRS message and updates the wallet balance.
     * @param b                 The block array from the updated blocks requested.
     * @return                  The updated Wallet balance after these blocks.
     */
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
        //Update the wallet's balance.
        Wallet.balance = bal;
        return bal;
    }
    
    /**
     * @return                  The current balance of the wallet software.
     */
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
        writeBlockChain(new File(CHAIN_FILENAME));
        return balance;
    }
    
    /**
     * Serializes the block chain object -- idea was to obfuscate the representation,
     * but in reality it has very little effect, and JSON looks nicer so do that also.
     * @param file                  The file the block chain is serialized to.
     */
    private static void writeBlockChain(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(block_chain);
            oos.flush();
            oos.close();
            
            //Write the blockchain to json file for readability.
            writeBlockChain2Json(new File(JSON_CHAIN_FILE));
            
        } catch (FileNotFoundException FNFE) { FNFE.printStackTrace(); 
        } catch (IOException IOE) { IOE.printStackTrace(); } 
    }
    
    /**
     * Writes the block chain object to a json file.
     * @param file                     The file name to which we want to write.
     */
    private static void writeBlockChain2Json(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(block_chain, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Read Serialized BlockChain object from file and set it to be the current blockchain.
     */
    private BlockChain readBlockChain(File f) throws FileNotFoundException {
        BlockChain bc = null;
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
                bc = (BlockChain) ois.readObject();
                return bc;
            } catch (IOException IOE) { IOE.printStackTrace(); 
            } catch (ClassNotFoundException CNFE) {CNFE.printStackTrace();}
        } else {
            WalletConnection.sendMessage(new Message("RQBC;"));
        }
        
        return bc;
    }
    
    /**
     * @param s                     Takes a raw string from an incoming BCRS msg
     * in which the raw data is a json representation of the current block chain.
     */
    public static void readBlockChainFromStream(String s) {
        Gson g = new Gson();
        BlockChain bc = new BlockChain();
        block_chain = g.fromJson(s, bc.getClass());
        System.out.println("Latest Block Hash: "+block_chain.getLastHash());
        try {
            System.out.println("Latest Block Num: "+block_chain.getLatestBlockNumber());
        } catch (Exception E) {}
    }
    
    /**
     * @param s                     Takes a raw string from an incoming BKRS msg
     * where the raw string data attached is a json representation of a Block[].
     */
    public static void readBlocksFromStream(String s) {
        Gson g = new Gson();
        Block[] b = g.fromJson(s, Block[].class);
        for (int i = b.length-1; i >= 0; i--) {
            //adds from the bottom of the array to maintain order of the block chain.
            // As the miner will just send the array, added extra check to ensure it only adds blocks it doesnt already have.
            try {
                int blkNum = block_chain.getLatestBlockNumber();
                if (b[i].getBlockNumber() > blkNum) {
                    block_chain.addBlock(b[i]);
                }
            } catch (Exception E) {}
        }
        //Updates the balance based on these new blocks.
        updateBalance(b);
    }
}