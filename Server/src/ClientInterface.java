import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.*;

public interface ClientInterface extends Remote {
    void setClientName(String username) throws RemoteException;
    String getClientName() throws RemoteException;
    List<List<?>> getBoardStatus() throws RemoteException; // Send board status to server after drew
    void updateBoardStatus(ArrayList<Object> shapes, ArrayList<Color> colors, ArrayList<Point> shapePositions) throws RemoteException;
    // If someone is drawing but not finished, sync that drawing process
    void updatePartialDraw(Shape curDrawing, Color curColor, String curShape) throws RemoteException;
    void closeByServer() throws RemoteException; // Server closed, close all clients
    void kicked() throws RemoteException; // Forced to close by the server
    void clear() throws RemoteException; // Server requests to clear the board status to empty
    // Update the list of other online users
    void updateClientsList(List<String> clientList) throws RemoteException;
}