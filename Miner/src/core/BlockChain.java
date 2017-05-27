package core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Samuel Heath
 */
public class BlockChain implements Serializable {
    
    private ArrayList<Block> block_chain = new ArrayList<Block>(20);
    
    public BlockChain() {}      // Empty Constructor.
     
    public BlockChain(Block blockOfGenesis) { block_chain.add(blockOfGenesis); }
    
    /**
     * Adds a new block to the front of the BlockChain.
     * @param b                 The block to be added.
     */
    public void addBlock(Block b) { this.block_chain.add(0, b); }
    
    /**
     * 
     * @param last_block_number     The last block number the the user has.
     * @return                      All the blocks on he block chain after some number
     */
    public Block[] getBlocksFromBlockNumber(int last_block_number) {
        try {
            if (last_block_number > getLatestBlockNumber() || last_block_number < 0) {
                // Error
                return (Block[]) block_chain.toArray();
            } else if (last_block_number == getLatestBlockNumber()) {
                return null; //Nothing
            }else {
                int blockDiff = last_block_number - getLatestBlockNumber();
                Block[] blocks = new Block[blockDiff];
                for (int i = blockDiff; i> 0; i++) {
                    blocks[i-1] = block_chain.get(i);
                }
                return blocks;
            }
        } catch (Exception E) {
            System.out.println("Incorrect Block Hash Requested.");
        }
        return null;
    } 
    
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
    
    public int getLatestBlockNumber() throws Exception { 
        if (this.block_chain.size() > 0) {
            return this.block_chain.get(0).getBlockNumber(); 
        } else {
            throw new Exception("No Block Chain Object stored.");
        }
    }
    
    /**
     * @return                  The hash of the last block in the system.
     */
    public String getLastHash() {
        return block_chain.get(0).getHash();
    }
}
