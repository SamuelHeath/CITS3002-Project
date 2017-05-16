package core;

/**
 *
 * @author Samuel Heath
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MinerIO mio;
        Miner m;
        if (args.length == 2) {
            mio = new MinerIO();
            m = new Miner(Integer.parseInt(args[1]));
            new Thread(m).start();
            Server s = new Server(Integer.parseInt(args[0]));
            new Thread(s).start();
        } else if (args.length == 1) {
            m = new Miner();
            new Thread(m).start();
            Server s = new Server(Integer.parseInt(args[0]));
            new Thread(s).start();
        } else {
            System.out.println("Please enter atleast 1 arguments");
            System.exit(0);
        }
    }

}
