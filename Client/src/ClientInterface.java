import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.*;

public interface ClientInterface extends Remote {
//    void updateWhiteboard(ClientInterface client) throws RemoteException;
    void setClientName(String username) throws RemoteException;
    String getClientName() throws RemoteException;
    List<List<?>> getBoardStatus() throws RemoteException;
    void updateBoardStatus(ArrayList<Object> shapes, ArrayList<Color> colors, ArrayList<Point> shapePositions) throws RemoteException;
    void updatePartialDraw(Shape curDrawing, Color curColor, String curShape) throws RemoteException;
    void closeByServer() throws RemoteException;
    void kicked() throws RemoteException;
    void clear() throws RemoteException;
    void updateClientsList(List<String> clientList) throws RemoteException;
    void test() throws RemoteException;
}