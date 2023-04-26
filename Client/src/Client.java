import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.net.ConnectException;
import java.util.Random;
import javax.swing.SwingUtilities;

public class Client {
    private static final Random random = new Random();
    // A unique client name for using in the server
    private static String username;
    private static String serverAddress;
    private static String portNumber;
    private static boolean connected = false;

    public static void main(String[] args) {
        // Pop up a login panel when open the program
        login(ClientGUI.login());
        System.out.println("login");
        while (!connected) { // If client cannot connect to the server, keep retrying
            try {
                ClientObj client = new ClientObj();
                ServerInterface server = (ServerInterface) Naming.lookup("rmi://" + serverAddress + ":" + portNumber + "/ServerRemoteObj");
                connected = true;
                System.out.println("Response from the remote object: " + server.sayHello());
                int isJoin = server.join(username, client);
                while (true) {
                    if (isJoin == 2) {
                        System.out.println("name duplicate");
                        ClientGUI.loginError("Username already exists! Please try other names.");
                        login(ClientGUI.login()); // Retry
                    } else if (isJoin == 1) {
                        ClientGUI.loginError("Manager refused your connection!");
                        login(ClientGUI.login()); // Retry
                    } else { // Successfully joined in
                        System.out.println("login success");
                        break;
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    ClientGUI clientGUI = new ClientGUI();
                    clientGUI.setVisible(true);
                });

                System.out.println("Joined the server! Your name: " + username);
            } catch (RemoteException e) {
                if (e.getCause() instanceof ConnectException) {
                    ClientGUI.loginError("Server not found! Please try other servers");
                    login(ClientGUI.login()); // Retry
                } else {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                System.err.println("MalformedURLException: " + e.getMessage());
            } catch (NotBoundException e) {
                System.err.println("Remote object name is not currently bound: " + e.getMessage());
            }
        }
    }

    private static void login(String[] loginInfo) {
        username = loginInfo[0];
        serverAddress = loginInfo[1];
        portNumber = loginInfo[2];
    }
}
