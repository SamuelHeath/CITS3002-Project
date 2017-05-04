package core;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        if (args.length == 2) {
            InetAddress netIP = InetAddress.getLoopbackAddress();
            try {
                netIP = InetAddress.getByName(args[0]);
            } catch (UnknownHostException UHE) {
                UHE.printStackTrace();
            }
            // Check to see if the input address is a valid address.
            if (Miner.checkAddress(netIP)) {
                m = new Miner(netIP, Integer.parseInt(args[1]));
            } else {
                System.out.println("Enter Valid IP");
                System.exit(0);
            }
        } else {
            System.out.println("Please enter atleast 2 arguments");
            System.exit(0);
        }
    }

}
