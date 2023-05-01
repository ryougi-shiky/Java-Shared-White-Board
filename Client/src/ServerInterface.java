import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public interface ServerInterface extends Remote {
    String sayHello() throws RemoteException;
    int join(String clientName, ClientInterface client) throws RemoteException;
    void draw(ClientInterface client) throws RemoteException;
    void partialDraw(ClientInterface client, Shape curDrawing, Color curColor, String curShape) throws RemoteException;
    ArrayList<Object> getServerShapes() throws RemoteException;
    ArrayList<Color> getServerColors() throws RemoteException;
    ArrayList<Point> getServerShapesPositions() throws RemoteException;
    void leave(ClientInterface client) throws RemoteException;
    void closeServer() throws RemoteException;
//    void broadcastMessage(String message) throws RemoteException;
//    void updateWhiteboard(WhiteboardItem item) throws RemoteException;
//    List<WhiteboardItem> getWhiteboardItems() throws RemoteException;
}



