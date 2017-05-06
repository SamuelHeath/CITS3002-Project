package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
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
             //m = new Miner(Integer.parseInt(args[0]));
        } else {
            System.out.println("Please enter atleast 1 arguments");
            System.exit(0);
        }
        try {
        ServerSocketFactory ServerSockFactory = (ServerSocketFactory) ServerSocketFactory.getDefault();
        ServerSocket ServerSock = (ServerSocket) ServerSockFactory.createServerSocket(9999);
        Socket clientSock = (Socket) ServerSock.accept();
        
        InputStream inputstream = clientSock.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        
        String string = null;
        while ((string = bufferedreader.readLine()) != null) {
            System.out.println(string);
            System.out.flush();
        }
        
        } catch (IOException IOE) { IOE.printStackTrace(); }
    }

}
