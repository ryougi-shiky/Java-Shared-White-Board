import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.lang.NumberFormatException;

public class Server {
    public static void main(String[] args) {
        int portNumber = ServerGUI.setup();
        System.out.println("System starting...");
        try {
            LocateRegistry.createRegistry(portNumber);
            ServerGUI serverGUI = new ServerGUI();
            ServerRemoteObj boardServer = new ServerRemoteObj(serverGUI);
            Naming.rebind("rmi://localhost:" + portNumber + "/ServerRemoteObj", boardServer);
            serverGUI.setServerInterface(boardServer);
            System.out.println("Server is ready.");
        } catch (RemoteException | MalformedURLException e){
            e.printStackTrace();
        }
    }
}


