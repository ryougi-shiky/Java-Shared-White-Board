import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRemoteObj extends UnicastRemoteObject implements ServerInterface {
    protected ServerRemoteObj() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello from the remote object!";
    }

     @Override
    public synchronized String join(String username, Client client) {
        // Add client to the list, assign a unique ID, and return it
         return "join";
    }

    @Override
    public synchronized void leave(String username) {
        // Remove client from the list
    }

    @Override
    public synchronized void broadcastMessage(String message) {
        // Broadcast message to all clients
    }

    @Override
    public synchronized void updateWhiteboard(WhiteboardItem item) {
        // Update whiteboard state and broadcast the item to all clients
    }

    @Override
    public synchronized List<WhiteboardItem> getWhiteboardItems() {
        // Return the current whiteboard state
        return null;
    }
}
