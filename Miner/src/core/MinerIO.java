package core;

import com.google.gson.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.xml.bind.DatatypeConverter;

/**
 * Handles the reading and writing of the serializable block-chain whilst also
 * facilitating the exportation to other formats e.g. xml and json.
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
 */
public class MinerIO {
    
    private static BlockChain block_chain = new BlockChain();
    private static final String CHAIN_FILENAME = "block-chain.dat";
    private static final String JSON_CHAIN_FILE = "block-chain.json";
    
    public MinerIO() {
        File f = new File(CHAIN_FILENAME);
        if (f.exists()) {
            try {
                //Reads serialized blockchain stored on computer
                System.out.println("Reading Block Chain");
                block_chain = readBlockChain(f);
                System.out.println("Block Chain Size: " + block_chain.getBlocks().size());
            } catch (FileNotFoundException FNFE) {}
        } else {
            byte[] zeroBytes = new byte[32]; //Initially has all 0's.
            
            //If object not found then we create Genesis block giving all users 
            //on the system some coins, then writeBlockChain the object and read it again.
            Transaction init_trans = new Transaction(getHex(zeroBytes),getHex(zeroBytes),
                    25.0,getHex(zeroBytes));
            
            //The actual coinbase address of the original Bitcoin Block.
            Block b = new Block(getHex(zeroBytes),"1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
                    (int)(System.currentTimeMillis()/1000L),0,1,new Transaction[] {init_trans});
            
            b.setHash(getHex(zeroBytes));
            b.setMerkleRoot(zeroBytes);
            
            block_chain = new BlockChain(b);
            MinerIO.writeBlockChain(f);
        }
    }
    
    public String getHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
    
    /**
     * @return                  
     */
    public static BlockChain getBlockChain() { return block_chain; }
    
    /**
     * The public interface to write the block chain
     */
    public static void writeBlockChain() {
        MinerIO.writeBlockChain(new File(CHAIN_FILENAME));
    }
    
    /**
     * @param file                  The where to store the serialized block chain.
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
     * @return                  Block Chain as a single string to send to Wallets.
     */
    public static String getBlockChainAsJson() {
        Gson g = new Gson();
        return g.toJson(block_chain, block_chain.getClass());
    }
    
    /**
     * Writes the Block Chain object to json.
     * @param f                     The file in which to store the json representation.
     */
    private static void writeBlockChain2Json(File f) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(f)) {
            gson.toJson(block_chain, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads the file in the directory and creates the block-chain as Block
     * objects in the blockChain variable.
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
    
}