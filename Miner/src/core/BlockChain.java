package core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Samuel Heath
 */
public class BlockChain implements Serializable {
    
    private ArrayList<Block> block_chain = new ArrayList<Block>(20);
    
    public BlockChain() {} // Nothing
     
    public BlockChain(Block blockOfGenesis) { block_chain.add(blockOfGenesis); }
    
    /**
     * Adds a new block to the front of the BlockChain.
     * @param b                 The block to be added.
     */
    public void addBlock(Block b) { this.block_chain.add(0, b); }
    
    public ArrayList<Block> getBlocksFromLastHash(String last_hash) {
        ArrayList<Block> chain = new ArrayList(1);
        boolean found = false;
        for (int i = block_chain.size()-1; i >= 0; i--) {
            if (block_chain.get(i).getHash().equals(last_hash)) {
                found = true;
            }
            if (found) {
                chain.add(0,block_chain.get(i));
            }
        }
        //Return null if chain is empty or the chain if false.
        return chain.isEmpty() ? block_chain : chain;
    }
    
    /**
     * @return                  The whole block chain.
     */
    public ArrayList<Block> getBlocks() { return this.block_chain; }
    
    
    /**
     * @return                  The hash of the last block in the system.
     */
    public String getLastHash() {
        return block_chain.get(block_chain.size()-1).getHash();
    }
}
