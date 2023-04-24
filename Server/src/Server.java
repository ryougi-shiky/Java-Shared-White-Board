import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;


public class Server {
    public static void main(String[] args) {
//        String serverIPAddress = args[0];
//        int serverPort = Integer.parseInt(args[1]);
//        String username = args[2];
        ServerGUI serverGUI = new ServerGUI();

        try {
            LocateRegistry.createRegistry(1099);
            ServerRemoteObj boardServer = new ServerRemoteObj(serverGUI);
            Naming.rebind("ServerRemoteObj", boardServer);
            System.out.println("Server is ready.");
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e){
            System.err.println("MalformedURLException: " + e.getMessage());
        }
    }
}


