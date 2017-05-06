package core;

/**
 *
 * @author Sam
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Miner m;
        if (args.length == 1) {
             m = new Miner(Integer.parseInt(args[0]));
        } else {
            System.out.println("Please enter atleast 1 arguments");
            System.exit(0);
        }
    }

}
