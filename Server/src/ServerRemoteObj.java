import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

public class ServerRemoteObj extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGUI;
    private List<String> clientList;
    private ArrayList<Object> shapes;
    private ArrayList<Color> colors;
    private ArrayList<Point> shapePositions;
    private List<ClientInterface> clients;
    private static final Random random = new Random();

    public ServerRemoteObj(ServerGUI serverGUI) throws RemoteException {
        super();
        this.serverGUI = serverGUI;
        clientList = new ArrayList<>();
        clients = new ArrayList<>();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello from the remote object!";
    }

    public synchronized int join(String clientName, ClientInterface client) {
        if (clientList.contains(clientName)) {
            return 2; // Client name duplicate
        }
        // Waiting for server's approval to join
        if (approveJoin(clientName)) {
            // Add client to the list
            serverGUI.addClient(clientName);
            clientList.add(clientName);
            clients.add(client);
            try {
                client.test();

            } catch (RemoteException e){
                e.printStackTrace();
            }
            return 0;
        } else { // Manager refused
            return 1;
        }
    }

    private static Boolean approveJoin(String clientName) {
        int response = JOptionPane.showConfirmDialog(
                null,
                clientName + " request to join the whiteboard",
                "New Client Request",
                JOptionPane.YES_NO_OPTION
        );
        return response == JOptionPane.YES_OPTION;
    }

    public synchronized void draw(ClientInterface client){

    }
//
//    @Override
//    public synchronized void leave(String username) {
//        // Remove client from the list
//    }
//
//    @Override
//    public synchronized void broadcastMessage(String message) {
//        // Broadcast message to all clients
//    }
//
//    @Override
//    public synchronized void updateWhiteboard(WhiteboardItem item) {
//        // Update whiteboard state and broadcast the item to all clients
//    }
//
//    @Override
//    public synchronized List<WhiteboardItem> getWhiteboardItems() {
//        // Return the current whiteboard state
//        return null;
//    }
}
