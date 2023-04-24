import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerRemoteObj extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGUI;
    private List<String> clientList;
    private static final Random random = new Random();
    protected ServerRemoteObj(ServerGUI serverGUI) throws RemoteException {
        super();
        this.serverGUI = serverGUI;
        clientList = new ArrayList<>();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello from the remote object!";
    }

    private static String generateName(){
        // Generates a random number between 1 and 1000 (inclusive)
        int randomNumber = random.nextInt(1000) + 1;
        return "Client " + randomNumber;
    }
    public synchronized String join() {
        String clientName = generateName();
        // If client name duplicate, generate a new name.
        while (clientList.contains(clientName)){
            clientName = generateName();
        }
        // Waiting for server's approval to join
        if (approveJoin(clientName)){
            // Add client to the list
            serverGUI.addClient(clientName);
            return clientName;
        } else {
            return null;
        }
    }
    private static Boolean approveJoin(String clientName){
        int response = javax.swing.JOptionPane.showConfirmDialog(
            null,
            clientName + " request to join the whiteboard",
            "New Client Request",
            javax.swing.JOptionPane.YES_NO_OPTION
        );
        return response == javax.swing.JOptionPane.YES_OPTION;
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
