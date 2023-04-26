import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
//    void updateWhiteboard(ClientGUI gui) throws RemoteException;
    void test() throws RemoteException;
}