import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
public class ClientObj extends UnicastRemoteObject implements ClientInterface{
    // Constructor, GUI, and event listeners
    private ClientGUI clientGUI;
    public ClientObj() throws RemoteException {
        super();
    }
    public void updateWhiteboard(ClientGUI gui) {
        // Update the local whiteboard state
    }

    public void test(){
        System.out.println("You are under the server's control");
    }

    // Additional methods for advanced features
}
