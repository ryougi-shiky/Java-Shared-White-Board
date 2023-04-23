import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    String sayHello() throws RemoteException;
    String join(String username, WhiteboardClient client) throws RemoteException;
    void leave(String username) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;
    void updateWhiteboard(WhiteboardItem item) throws RemoteException;
    List<WhiteboardItem> getWhiteboardItems() throws RemoteException;
}

public interface ClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
    void updateWhiteboard(WhiteboardItem item) throws RemoteException;
    // Additional methods for advanced features
}


