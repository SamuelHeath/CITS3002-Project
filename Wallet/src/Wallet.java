public class Wallet {
    public Wallet() {}
    
    public static boolean checkTransactionAmount(Double amount) {
        if (amount >= 0.00000001) {
            if (WalletIO.getBalance()-amount >= 0) {
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