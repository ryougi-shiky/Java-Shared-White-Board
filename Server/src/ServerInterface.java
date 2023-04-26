import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

public interface ServerInterface extends Remote {
    String sayHello() throws RemoteException;
    int join(String clientName, ClientInterface client) throws RemoteException;
    void draw(ClientInterface client) throws RemoteException;
//    void leave(String username) throws RemoteException;
//    void broadcastMessage(String message) throws RemoteException;
//    void updateWhiteboard(WhiteboardItem item) throws RemoteException;
//    List<WhiteboardItem> getWhiteboardItems() throws RemoteException;
}



