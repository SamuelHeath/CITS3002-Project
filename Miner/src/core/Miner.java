package core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Miner which assembles blocks from a single transaction and performs calculations
 * to find the hash of the block.
 * @author Samuel Heath
 */
public class Miner implements Runnable {
    
    private static Block currentBlock;
    private static String coinBaseAddress;
    private static int proof_difficulty = 3; //Default difficulty for miner
    private static ArrayList<Transaction> transactions = new ArrayList(1);
    
    /**
     * Default initialisation of the Miner.
     */
    public Miner() {
        KeyPairGen.readKeys();
        coinBaseAddress = KeyPairGen.getPublicKeyAddress();
        System.out.println("Coin Base Address: "+coinBaseAddress);
        //GET CURRENT BLOCK FROM BLOCKCHAIN. SEE MINERIO
    }
    
    /**
     * Initialises the Miner object so that it has a set difficulty
     * @param difficulty        The number of 0's required at the front of a hashed message.
     */
    public Miner(int difficulty) {
        if (difficulty > 31) {
            proof_difficulty = 31;
        } else proof_difficulty = difficulty;
        KeyPairGen.readKeys();
        coinBaseAddress = KeyPairGen.getPublicKeyAddress();
        System.out.println(coinBaseAddress);
    }
    
    @Override
    public void run() {
        System.out.println("Miner Difficulty: " + proof_difficulty);
    }
      
    /**
     * Performs the proof of work on some input message.
     * @param init_block
     * @return                  
     */
    public static void proofOfWork(Block init_block) {
        Block b = init_block;
        byte[] prevHash = b.getPreviousHash().getBytes(StandardCharsets.US_ASCII);
        byte[] header_bytes = blockHeader2Bytes(b,prevHash);
        long init_time = System.currentTimeMillis();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] double_hash = sha256.digest(sha256.digest(header_bytes));
            System.out.print("Start Hash: ");
            System.out.println(Base58Check.encode(double_hash,true));
            System.out.println("---------------------------------------");
            long init_time2 = System.currentTimeMillis();
            long numHashes = 1; // Stores a count of the number of hashes
            while (!checkHashedBits(double_hash)) {
                
                if (numHashes == Long.MAX_VALUE) {numHashes = 0;}
                numHashes++;
                
                if (System.currentTimeMillis()-init_time2 > 15000) {
                    System.out.println("Hashes/sec "+numHashes/30);
                    numHashes=0;
                    init_time2 = System.currentTimeMillis();
                }
                
                if (Integer.MAX_VALUE == b.getNonce()) {
                    //Add Timestamp & start nonce over again
                    b.setTimeStamp((int) (System.currentTimeMillis() / 1000L));
                    b.setNonce(0);
                }
                b.setNonce(b.getNonce()+1);
                header_bytes = blockHeader2Bytes(b,prevHash);
                sha256.update(header_bytes);
                double_hash = sha256.digest();
            }
            b.setHash(Base58Check.encode(double_hash,true));
            System.out.println("End Hash:   " + b.getHash());
            System.out.println("Time: " + (float)(System.currentTimeMillis() - init_time)/60000 + "min " + "Nonce: " + b.getNonce());
        } catch (NoSuchAlgorithmException NSAE) {}
        currentBlock = b;
        System.out.println(currentBlock.getHash());
    }
    
    /**
     * Checks to see if the hasher has set the first n bytes to 0, where n is
     * equal to the difficulty factor and less than 32 (256bit -> 32 bytes).
     * @param hash              The SHA-256 hashed message.
     * @return                  Return true if the hasher hasn't been met.
     */
    private static boolean checkHashedBits(byte[] hash) {
        for (int i = 0; i < proof_difficulty; i++) {
            if (hash[i] != 0) return false;
        }
        if ((hash[proof_difficulty] & 0xF) != 0) return false;
        return true;
    }
    
    /**
     * 
     * @param b
     * @param const_bytes                   The bytes in the header that don't change.
     * @return 
     */
    private static byte[] blockHeader2Bytes(Block b, byte[] const_bytes) {
        byte[] timeStamp = genByteArrFromInt(b.getTimeStamp());
        byte[] nonce = genByteArrFromInt(b.getNonce());
        return concatByteArr(concatByteArr(const_bytes,timeStamp),nonce);
    }
    
    /**
     * @return                  A 4 byte array randomly generated from an int.
     */
    private static byte[] genByteArrFromInt(int current_nonce) {
        return ByteBuffer.allocate(4).putInt(current_nonce).array();
    }
    
    /**
     * @param message           The message in byte array.
     * @param nonce             The nonce in byte array.
     * @param time_stamp
     * @return                  Whether or not the proof of work is valid.
     */
    public static boolean checkProofOfWork(byte[] message, byte[] nonce, int time_stamp) {
        
        byte[] combined = concatByteArr(concatByteArr(message,nonce),genByteArrFromInt(time_stamp));
        try {
            MessageDigest hashd = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = hashd.digest(hashd.digest(combined));
            for (int i=0; i < proof_difficulty; i++) {
                if (encodedhash[i] != 0) return false;
            }
            if ((encodedhash[proof_difficulty] & 0xF) != 0) return false;
        } catch (NoSuchAlgorithmException NSAE) {}
        return true;
    }
    
    /**
     * Concatinates the nonce byte array with the message byte array, allowing
     * SHA-256 to work on the whole array.
     * @param a                     The msg byte array.
     * @param b                     The nonce byte array to be appended.
     * @return                      The resulting byte array after concatination.
     */
    private static byte[] concatByteArr(byte[] a, byte[] b) {
        byte[] concatArr = new byte[a.length+b.length];
        System.arraycopy(a, 0, concatArr, 0, b.length);
        System.arraycopy(b, 0, concatArr, a.length, b.length);
        return concatArr;
    }
    
    /**
     * 
     * @param transactionMessage
     * @return 
     */
    private static Block getTransactionBlock(String transactionMessage) {
        String[] transComp = transactionMessage.replace("'", "").split("-");
        Transaction t = new Transaction(transComp[0],transComp[1],Double.valueOf(transComp[2]),transComp[3]);
        if (t.verifySignature()) {
            transactions.add(t);
        }
        
        Transaction coinBaseTrans = new Transaction(coinBaseAddress,coinBaseAddress,(double)25.0);
        coinBaseTrans.signCoinBaseTransaction(); //Edits the signature field of this object to be signed.
        //transactions.add(0,coinBaseTrans); //Inserts the Coin Base Transaction at the start of the transactions.
        Transaction[] block_transactions = new Transaction[transactions.size()];
        for (int i = 0; i < block_transactions.length; i++) {
            block_transactions[i] = transactions.remove(0); //Take off the first element.
        }
        return new Block(getPreviousHash(),coinBaseAddress,(int)(System.currentTimeMillis()/100L),0,transactions.size(),block_transactions);
    }
    
    /**
     * @return                      The last hash on the blockchain.
     */
    private static String getPreviousHash() {
        return MinerIO.getBlockChain().getLastHash();
    }
    
    /**
     * 
     * @param transaction 
     */
    public static void transactionMessage(Message transaction) {
        currentBlock = getTransactionBlock(transaction.getRawData());
        System.out.println("Previous Hash: "+currentBlock.getPreviousHash());
        System.out.println("Coin Base Address: "+currentBlock.getCoinBase());
        proofOfWork(currentBlock);
        MinerIO.getBlockChain().addBlock(currentBlock);
        MinerIO.write();
        Server.broadcastMessage(new Message("BCST:"+currentBlock.blockToString()));
    }
    
    /**
     * @param msg
     * @return                  The current longest Blockchain of the system.
     */
    public static ArrayList<Message> blockChainRequested(Message msg) {
        ArrayList<Message> response = new ArrayList(1);
        if (msg.getRawData().equals("")) {
            for (Block b: MinerIO.getBlockChain().getBlocks()) {
                response.add(new Message("BCRS:"+b.blockToString()));
            }
        } else {
            for (Block b : MinerIO.getBlockChain().getBlocksFromLastHash(msg.getRawData())) {
                response.add(new Message("BCRS:"+b.blockToString()));
            }
        }
        return response;
    }
    
}