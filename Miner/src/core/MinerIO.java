package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Handles the reading and writing of the serializable block-chain whilst also
 * facilitating the exportation to other formats e.g. xml and json.
 * @author Samuel Heath
 */
class MinerIO {
    
    private static BlockChain block_chain = new BlockChain();
    private static final String CHAIN_FILENAME = "block-chain.dat";
    
    public MinerIO() {
        File f = new File(CHAIN_FILENAME);
        if (f.exists()) {
            try {
                //Reads serialized blockchain stored on computer
                System.out.println("Reading Block Chain");
                block_chain = readBlockChain(f);
                System.out.println("Size: " + block_chain.getBlocks().size());
                for (Block b : block_chain.getBlocks()) {
                    System.out.println(b.blockToString());
                }
            } catch (FileNotFoundException FNFE) {}
        } else {
             //If object not found then we create Genesis block giving all users 
            //on the system some coins, then write the object and read it again.
            Transaction init_trans = new Transaction("000","000",50,"000");
            Block b = new Block("000","000",(int)(System.currentTimeMillis()/1000L),0,1,new Transaction[] {init_trans});
            b.setHash("000");
            System.out.println("Generated Genisis Block: " + b.blockToString());
            block_chain = new BlockChain(b);
            writeBlockChain(f);
        }

    }
    
    public static BlockChain getBlockChain() { return block_chain; }
    
    private static void writeBlockChain(File f) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(block_chain);
            oos.flush();
            oos.close();
            for (Block b : block_chain.getBlocks()) {
                System.out.println(b.blockToString());
            }
        } catch (FileNotFoundException FNFE) { FNFE.printStackTrace(); 
        } catch (IOException IOE) { IOE.printStackTrace(); } 
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