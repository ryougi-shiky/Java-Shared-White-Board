import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public interface ServerInterface extends Remote {
    int join(String clientName, ClientInterface client) throws RemoteException;
    public String getManagerName() throws RemoteException;
    // Receive client's drawn shapes
    void draw(ClientInterface client) throws RemoteException;
    void partialDraw(ClientInterface client, Shape curDrawing, Color curColor, String curShape) throws RemoteException;
    // Server sync board status to client
    ArrayList<Object> getServerShapes() throws RemoteException;
    ArrayList<Color> getServerColors() throws RemoteException;
    ArrayList<Point> getServerShapesPositions() throws RemoteException;
    void leave(ClientInterface client) throws RemoteException; // A client left the server
    boolean kickout(String clientName) throws RemoteException;
    void syncClientList() throws RemoteException; // Share online clients to all clients
    void closeServer() throws RemoteException;
    void fileSelect(String option) throws RemoteException; // Select operation in ServerGUI top menu "File"
}



