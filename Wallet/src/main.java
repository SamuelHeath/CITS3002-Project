import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

import net.Message;

public class main {
    
    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                SocketFactory sslsocketfactory = (SocketFactory) SocketFactory.getDefault();
                Socket sslsocket = (Socket) sslsocketfactory.createSocket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
                System.out.println("Connected to Network");
                
                InputStream serverstream = sslsocket.getInputStream();
                InputStreamReader serverstreamreader = new InputStreamReader(serverstream);
                BufferedReader bufferedserverreader = new BufferedReader(serverstreamreader);
                
                OutputStream outputstream = sslsocket.getOutputStream();
                PrintWriter pwrite = new PrintWriter(outputstream, true);
                
                // Create default message with the type broadcast.
                pwrite.println(new Message("BCST","Hey How Are You"));
                pwrite.flush();
                
                String string = null;
                while ((string = bufferedserverreader.readLine()) != null) {
                    Message m = new Message(string);
                    System.out.println(m.getRawData());
                    pwrite.println(m.toString());
                    pwrite.flush();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("You must enter atleast 2 variables");
            System.exit(-1);
        }
    }
}