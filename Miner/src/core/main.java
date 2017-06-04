package core;

/**
 *
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MinerIO mio;
        Miner m;
        if (args.length >= 2) {
            m = new Miner(Integer.parseInt(args[1]));
            if (args.length == 3 && args[2].equals("--nibble")) { 
                m.useNibbleInProof(false); 
                System.out.println("Checking the nibble as well"); 
            }
            new Thread(m).start();
        } else if (args.length == 1) {
            m = new Miner();
            new Thread(m).start();
        } else {
            System.out.println("Please enter atleast 1 arguments");
            System.exit(0);
        }
        mio = new MinerIO();
        Server s = new Server(Integer.parseInt(args[0]));
        new Thread(s).start();
    }

}
