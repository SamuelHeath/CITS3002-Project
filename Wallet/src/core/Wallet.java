package core;


import com.google.gson.Gson;
import java.util.Scanner;

/**
 * 
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
 */
public class Wallet implements Runnable {
    
    private static Message msg;
    public static boolean update = false;
    public static double balance;
    private static double trans_amount;
    private static String receiver_address;
    private WalletIO wio;
    
    @Override
    public void run() {
        
        WalletConnection.sendMessage(new Message("RQBC;"));
        
        //Waits for a response
        while (!update) {
        }
        
        //Sends the main message required if its not an update message.
        if (msg.getType().equals("TX")) {
            while (!checkTransactionAmount(trans_amount)) {
                System.out.print("Enter new transaction amount: ");
                Scanner s = new Scanner(System.in);
                trans_amount = s.nextDouble();
            }
            Transaction t = new Transaction(KeyPairGen.publicKey2String(KeyPairGen.getPublicKey()),receiver_address,trans_amount);
            t.signTransaction();
            
            if (t.verifySignature()) {
                WalletConnection.sendMessage(new Message("TX;"+new Gson().toJson(t, Transaction.class)));
                System.out.println("Sending Transaction to Miner!");
            } else { System.out.println("Couldn't verify signature."); System.exit(-1); }
        } else { System.exit(1); }
    }
    
    /**
     * Constructs the wallet software and then performs work.
     * @param args                  The runtime arguments passed to the wallet.
     */
    public Wallet(String[] args) {
        wio = new WalletIO();
        if (args.length == 3) {
            msg = new Message("RQBC;");
        } else if (args.length == 4) {
            String[] txmsg = args[3].split(" ");
            msg = new Message("TX;"); // Initially create a message header.
            if (txmsg.length == 2) {
                receiver_address = txmsg[0];
                trans_amount = Double.parseDouble(txmsg[1]);
            } else if (txmsg.length == 1) {
                receiver_address = KeyPairGen.getReceiverAddress();
                if (receiver_address.isEmpty()) {
                    System.out.println("Did not properly read the receiver address.");
                }
                System.out.println("Sending transaction too address " + receiver_address);
                trans_amount = Double.parseDouble(txmsg[0]);
            } else {
                System.out.println("Invalid Command");
                System.exit(1);
            }
        } else {
            System.out.println("You must enter atleast 3 arguments");
            System.exit(-1);
        }
    }
    
    public WalletIO getIO() { return this.wio; }
    
    public static void setResponse(boolean b) { update = b; }
    
    public static boolean checkTransactionAmount(Double amount) {
        if (amount >= 0.00000001) {
            if (balance-amount >= 0) {
                return true;
            } else { 
                System.out.println("\nInsufficient Funds. Please enter a valid amount of CTRL+C to exit.");
                return false;
            }
        } else {
            System.out.println("\nInvalid transaction amount! Please enter a positive input with a value of 1x10^-8");
            return false;
        }
    }
}