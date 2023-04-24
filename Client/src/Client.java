import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Random;
import javax.swing.SwingUtilities;

public class Client {
    private static final Random random = new Random();
    // A unique client name for using in the server
    private static String clientName;
    public static void main(String[] args) {
//        String serverIPAddress = args[0];
//        int serverPort = Integer.parseInt(args[1]);
//        String username = args[2];
        SwingUtilities.invokeLater(() -> {
            ClientGUI clientGUI = new ClientGUI();
            clientGUI.setVisible(true);
        });
        try {
            ServerInterface client = (ServerInterface) Naming.lookup("rmi://localhost/ServerRemoteObj");
            String response = client.sayHello();
            System.out.println("Response from the remote object: " + response);
            clientName = client.join();
            if (clientName == null){
                System.out.println("The manager refused your join request.");
                return;
            }
            System.out.println("Joined the server! Your name: " + clientName);
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e){
            System.err.println("MalformedURLException: " + e.getMessage());
        } catch (NotBoundException e){
            System.err.println("Remote object name is not currently bound: " + e.getMessage());
        }
    }


}
