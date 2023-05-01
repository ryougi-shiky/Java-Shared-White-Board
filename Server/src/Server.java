import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;


public class Server {
    public static void main(String[] args) {
        int portNumber = ServerGUI.setup();
        ServerGUI serverGUI = new ServerGUI();

        try {
            LocateRegistry.createRegistry(portNumber);
            ServerRemoteObj boardServer = new ServerRemoteObj(serverGUI);
            Naming.rebind("rmi://localhost:" + portNumber + "/ServerRemoteObj", boardServer);
            serverGUI.setServerInterface(boardServer);
            System.out.println("Server is ready.");
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e){
            System.err.println("MalformedURLException: " + e.getMessage());
        }
    }
}


