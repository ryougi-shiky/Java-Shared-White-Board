import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.swing.*;
import java.awt.*;

public class ServerRemoteObj extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGUI;
    public List<String> clientList; //Name of clients
    // White board status
    private ArrayList<Object> shapes;
    private ArrayList<Color> colors;
    private ArrayList<Point> shapePositions;
    public List<ClientInterface> clients; // Clients objects

    public ServerRemoteObj(ServerGUI serverGUI) throws RemoteException {
        super();
        this.serverGUI = serverGUI;
        clientList = new ArrayList<>();
        clients = new ArrayList<>();
        shapes = new ArrayList<>();
        colors = new ArrayList<>();
        shapePositions = new ArrayList<>();
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
                client.setClientName(clientName);
            } catch (RemoteException e) {
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

    public synchronized void syncBoardStatus(ClientInterface client) {
        try {
            for (ClientInterface restClient : clients) {
                // Sync the board status to the rest clients
                if (!restClient.getClientName().equals(client.getClientName())) {
                    restClient.updateBoardStatus(shapes, colors, shapePositions);
                    System.out.println(restClient.getClientName() + " sync");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Object> getServerShapes() {
        return shapes;
    }

    public ArrayList<Color> getServerColors() {
        return colors;
    }

    public ArrayList<Point> getServerShapesPositions() {
        return shapePositions;
    }

    public synchronized void draw(ClientInterface client) {
        List<List<?>> boardStatus = new ArrayList<>();
        try {
            boardStatus = client.getBoardStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        shapes = new ArrayList<Object>(boardStatus.get(0));
        colors = new ArrayList<Color>();
        shapePositions = new ArrayList<Point>();

        for (Object color : boardStatus.get(1)) {
            colors.add((Color) color);
        }
        for (Object point : boardStatus.get(2)) {
            shapePositions.add((Point) point);
        }
        try {
            System.out.println(client.getClientName() + " drew");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        syncBoardStatus(client);
    }

    public synchronized void partialDraw(ClientInterface client, Shape curDrawing, Color curColor, String curShape) {
        try {
            for (ClientInterface restClient : clients) {
                // Sync the board status to the rest clients
                if (!restClient.getClientName().equals(client.getClientName())) {
                    restClient.updatePartialDraw(curDrawing, curColor, curShape);
                    System.out.println(restClient.getClientName() + " sync partial draw");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void leave(ClientInterface client) {
        Iterator<ClientInterface> iterator = clients.iterator();
        while (iterator.hasNext()) {
            ClientInterface currentClient = iterator.next();
            if (currentClient.equals(client)) {
                try {
                    clientList.remove(client.getClientName());
                    iterator.remove(); // Safely remove the client from the list
                    serverGUI.removeClient(client.getClientName());
                    System.out.println(client.getClientName() + " left the server.");
                    break;
                } catch (RemoteException e) {
                    System.out.println("Error on client leaving server.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeServer() {
        for (ClientInterface client : clients) {
            try {
                client.closeByServer();
                System.out.println("server remote object notified clients to close windows");
            } catch (RemoteException e) {
                System.out.println("Error on closing clients window ");
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public boolean kickout(String clientName) {
        Iterator<ClientInterface> iterator = clients.iterator();
        while (iterator.hasNext()) {
            ClientInterface client = iterator.next();
            try {
                if (client.getClientName().equals(clientName)) {
                    clientList.remove(client.getClientName());
                    serverGUI.removeClient(client.getClientName());
                    client.kicked();
                    iterator.remove(); // Safely remove the client from the list
                    System.out.println(client.getClientName() + " was kicked out.");
                    return true;
                }
            } catch (RemoteException e) {
                System.out.println("Error on kicking out a client.");
                e.printStackTrace();
            }
        }
        return false;
    }
}
