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
    public static Block proofOfWork(Block init_block) {
        Block b = init_block;
        try {
            b.setMerkelRoot(Base58Check.encode(calculateMerkelRoot(b),false));
        } catch (NoSuchAlgorithmException NSAE) {}
        byte[] merkelRoot = b.getMerkelRoot(); // Adds variabillity
        System.out.println("Merkel Root: "+b.merkel2String());
        System.out.println(b.getPreviousHash());
        byte[] prevHash = b.getPreviousHash().getBytes(StandardCharsets.US_ASCII);
        byte[] const_header_bytes = concatByteArr(prevHash,merkelRoot);
        byte[] header_bytes = blockHeader2Bytes(b,const_header_bytes);
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
                header_bytes = blockHeader2Bytes(b,const_header_bytes);
                sha256.update(header_bytes);
                double_hash = sha256.digest();
            }
            b.setHash(Base58Check.encode(double_hash,true));
            System.out.println("End Hash:   " + b.getHash());
            System.out.println("Time: " + (float)(System.currentTimeMillis() - init_time)/60000 + "min " + "Nonce: " + b.getNonce());
        } catch (NoSuchAlgorithmException NSAE) {}
        return b;
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
    
     private static byte[] calculateMerkelRoot(Block b) {
        byte[] hash = new byte[32];
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            Transaction t = b.getTransactions()[1];
            byte[] sender = t.getSenderKey().getBytes(StandardCharsets.US_ASCII);
            byte[] receiver = t.getReceiverKey().getBytes(StandardCharsets.US_ASCII);
            byte[] sig = sha256.digest(t.getSignature().getBytes(StandardCharsets.US_ASCII));
            sha256.update(Miner.concatByteArr(Miner.concatByteArr(sender,receiver),sig));
            hash = sha256.digest();
            
            //Do hashing of the coinbase address;
            t = b.getTransactions()[0];
            receiver = t.getReceiverKey().getBytes(StandardCharsets.US_ASCII);
            sig = sha256.digest(t.getSignature().getBytes(StandardCharsets.US_ASCII));
            sha256.update(Miner.concatByteArr(receiver,sig));
            byte[] coinbaseHash = sha256.digest();
            sha256.update(Miner.concatByteArr(hash,coinbaseHash));
            hash = sha256.digest();
        } catch (NoSuchAlgorithmException NSAE) {}
        
        return hash;
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
    public static byte[] concatByteArr(byte[] a, byte[] b) {
        byte[] concatArr = new byte[a.length+b.length];
        System.arraycopy(a, 0, concatArr, 0, a.length);
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
        Transaction coinBaseTrans = new Transaction("0000",coinBaseAddress,(double)25.0);
        coinBaseTrans.signCoinBaseTransaction(); //Edits the signature field of this object to be signed.
        //if (t.verifySignature() && coinBaseTrans.verifySignature()) {
            
            
        //}
        transactions.add(t);
        transactions.add(0,coinBaseTrans);//Inserts the Coin Base Transaction at the start of the transactions.
        Transaction[] block_transactions = new Transaction[transactions.size()];
        for (int i = 0; i < block_transactions.length; i++) { block_transactions[i] = transactions.remove(0); }
        return new Block(getPreviousHash(),coinBaseAddress,(int)(System.currentTimeMillis()/1000L),0,block_transactions.length,block_transactions);
    }
    
    /**
     * @return                      The last hash on the blockchain.
     */
    private static String getPreviousHash() {
        return MinerIO.getBlockChain().getLastHash();
    }
    
    /**
     * Handles a message sent from a wallet to perform a transaction.
     * @param transaction           The transaction the wallet wants completed.
     */
    public static void transactionMessage(Message transaction) {
        Block currBlock = getTransactionBlock(transaction.getRawData());
        System.out.println("Transactions:"+currBlock.getTransactionCount());
        System.out.println("Previous Hash: "+currBlock.getPreviousHash());
        System.out.println("Coin Base Address: "+currBlock.getCoinBase());
        currBlock = proofOfWork(currBlock);
        MinerIO.getBlockChain().addBlock(currBlock);
        MinerIO.writeBlockChain();
        Server.broadcastMessage(new Message("BCRS;"+MinerIO.getBlockChainAsJson()));
    }
    
    /**
     * @param msg
     * @return                  The current longest Blockchain of the system.
     */
    public static Message blockChainRequested(Message msg) {
        /*if (msg.getRawData().equals("")) {
            for (Block b: MinerIO.getBlockChain().getBlocks()) {
                response.add(new Message("BCRS:"+b.blockToString()));
            }
        } else {
            for (Block b : MinerIO.getBlockChain().getBlocksFromLastHash(msg.getRawData())) {
                response.add(new Message("BCRS:"+b.blockToString()));
            }
        }
        return response;*/
        return new Message("BCRS;"+MinerIO.getBlockChainAsJson());
    }
    
}