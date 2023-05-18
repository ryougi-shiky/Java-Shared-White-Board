import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.*;
public class Manager extends UnicastRemoteObject implements ClientInterface{
    // Constructor, GUI, and event listeners
    public ClientGUI clientGUI;
    private String clientName;
    public Manager() throws RemoteException {
        super();
    }

    public void setClientName(String username) {
        // Update client name
        this.clientName = username;
    }

    public String getClientName() {
        // Get client name
        return clientName;
    }

    public List<List<?>> getBoardStatus() {
        return clientGUI.getBoardStatus();
    }

    public synchronized void updateBoardStatus(ArrayList<Object> shapes, ArrayList<Color> colors, ArrayList<Point> shapePositions){
        clientGUI.updateBoardStatus(shapes, colors, shapePositions);
    }

    public synchronized void updatePartialDraw(Shape curDrawing, Color curColor, String curShape){
        clientGUI.updatePartialDraw(curDrawing, curColor, curShape);
    }

    public void closeByServer(){
        clientGUI.closeByServer();
        System.out.println("client remote object notified client's GUI to close windows");
    }

    public void kicked(){
        clientGUI.kicked();
    }

    public void clear(){
        clientGUI.clear();
    }

    public void updateClientsList(List<String> clientList){
        try {
            clientGUI.updateClientsList(clientList);

        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
