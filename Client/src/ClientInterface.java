import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ClientInterface extends Remote {
//    void updateWhiteboard(ClientInterface client) throws RemoteException;
    void setClientName(String username) throws RemoteException;
    String getClientName() throws RemoteException;
    List<List<?>> getBoardStatus() throws RemoteException;
    void updateBoardStatus(ArrayList<Object> shapes, ArrayList<java.awt.Color> colors, ArrayList<java.awt.Point> shapePositions) throws RemoteException;
    void test() throws RemoteException;
}