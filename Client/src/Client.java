import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.net.ConnectException;
import javax.swing.SwingUtilities;
import java.net.UnknownHostException;
import java.lang.NumberFormatException;

public class Client {
    private static String username;
    private static String serverAddress;
    private static String portNumber;
    private static boolean connected = false;

    public static void main(String[] args) {
        // Pop up a login panel when open the program
        login(ClientGUI.login());
        boolean isValid = false;
        try {
            while (!isValid){
                if (Integer.parseInt(portNumber) < 49152 | Integer.parseInt(portNumber) > 65535 | !portNumber.matches("\\d*")){
                    ClientGUI.loginError("Invalid port number! Please try other servers");
                    login(ClientGUI.login()); // Invalid port number, retry
                    isValid = false;
                } else {
                    isValid = true;
                }
            }
        } catch (NumberFormatException e){
            isValid = false;
            ClientGUI.loginError("Invalid port number! Please try other servers");
            login(ClientGUI.login()); // Invalid port number, retry
            e.printStackTrace();
        }

        System.out.println("login");

        while (!connected) { // If client cannot connect to the server, keep retrying
            try {
                ClientObj client = new ClientObj();
                client.setClientName(username);
                ServerInterface server = (ServerInterface) Naming.lookup("rmi://" + serverAddress + ":" + portNumber + "/ServerRemoteObj");
                connected = true;
                System.out.println("Connected to the server ");

                int isJoin = server.join(username, client);
                while (true) {
                    if (isJoin == 2) {
                        System.out.println("name duplicate");
                        ClientGUI.loginError("Username already exists! Please try other names.");
                        login(ClientGUI.login()); // Retry
                        isJoin = server.join(username, client);
                    } else if (isJoin == 1) {
                        ClientGUI.loginError("Manager refused your connection!");
                        login(ClientGUI.login()); // Retry
                        isJoin = server.join(username, client);
                    } else { // Successfully joined in
                        System.out.println("login success");
                        break;
                    }
                }

                // set up whiteboard after logged in
                SwingUtilities.invokeLater(() -> {
                    client.clientGUI = new ClientGUI(server, client);
                    try {
                        server.syncClientList();
                        client.updateBoardStatus(server.getServerShapes(), server.getServerColors(), server.getServerShapesPositions());
                    } catch (RemoteException e) {
                        System.out.println("Error on syncing server white board");
                        e.printStackTrace();
                    }
                    client.clientGUI.setVisible(true);
                });
                System.out.println("Joined the server! Your name: " + username);
            } catch (NotBoundException e) {
                System.err.println("Remote object name is not currently bound: " + e.getMessage());
                e.printStackTrace();
                ClientGUI.loginError("Server address or port invalid! Please try other servers");
                login(ClientGUI.login()); // Retry
            } catch (RemoteException e) {
                if (e.getCause() instanceof ConnectException) {
                    ClientGUI.loginError("Server not found! Please try other servers");
                    login(ClientGUI.login()); // Retry
                } else if (e.getCause() instanceof UnknownHostException) {
                    ClientGUI.loginError("Server not found! Please try other servers");
                    login(ClientGUI.login()); // Retry
                } else {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                System.err.println("MalformedURLException: " + e.getMessage());
                e.printStackTrace();
                ClientGUI.loginError("Server not found! Please try other servers");
                login(ClientGUI.login()); // Retry
            }
        }
    }

    private static void login(String[] loginInfo) {
        username = loginInfo[0];
        serverAddress = loginInfo[1];
        portNumber = loginInfo[2];
    }
}
