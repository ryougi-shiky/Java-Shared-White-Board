import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRemoteObj extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGUI;
    protected ServerRemoteObj(ServerGUI serverGUI) throws RemoteException {
        super();
        this.serverGUI = serverGUI;
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello from the remote object!";
    }

    public synchronized String join(String clientName) {
        // Add client to the list
        serverGUI.addClient(clientName);
        return "Successfully joined the server";
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
