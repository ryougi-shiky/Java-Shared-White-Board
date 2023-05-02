import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import org.json.simple.parser.*;
import org.json.simple.*;

import javax.imageio.*;
import java.io.*;

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

    private void save(File saveDir) {
        if (saveDir == null) {
            saveDir = new File("default_whiteboard_save.jpg");
        }

        byte[] imageBytes = null;
        try {
            imageBytes = clients.get(0).export();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (imageBytes != null) {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                try {
                    ImageIO.write(image, "jpg", saveDir);
                    System.out.println("Image saved: " + saveDir.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle("Save As");

        // Optional: Set a default file name and extension
        fileChooser.setSelectedFile(new File("default_whiteboard_save.jpg"));

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Save your data to the selected file
            save(selectedFile);
        }
    }

    private void open() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open WhiteBoard");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File openDir = fileChooser.getSelectedFile();

            if (openDir != null) {
                // Load the data from the selected file
                try {
                    try (FileReader reader = new FileReader(openDir)) {
                        JSONParser parser = new JSONParser();
                        JSONObject data = (JSONObject) parser.parse(reader);

                        // Clear the current board
                        shapes.clear();
                        colors.clear();
                        shapePositions.clear();

                        // Parse the shapes, colors, and shapePositions lists from file
                        JSONArray shapesJson = (JSONArray) data.get("shapes");
                        JSONArray colorsJson = (JSONArray) data.get("colors");
                        JSONArray shapePositionsJson = (JSONArray) data.get("shapePositions");

                        for (Object shape : shapesJson) {
                            JSONObject shapeObj = (JSONObject) shape;
                            String type = (String) shapeObj.get("type");
                            if ("line".equals(type)) {
                                Line2D.Float line = new Line2D.Float(
                                        ((Number) shapeObj.get("x1")).floatValue(),
                                        ((Number) shapeObj.get("y1")).floatValue(),
                                        ((Number) shapeObj.get("x2")).floatValue(),
                                        ((Number) shapeObj.get("y2")).floatValue()
                                );
                                shapes.add(line);
                            } else if ("ellipse".equals(type)) {
                                Ellipse2D.Float ellipse = new Ellipse2D.Float(
                                        ((Number) shapeObj.get("x")).floatValue(),
                                        ((Number) shapeObj.get("y")).floatValue(),
                                        ((Number) shapeObj.get("width")).floatValue(),
                                        ((Number) shapeObj.get("height")).floatValue()
                                );
                                shapes.add(ellipse);
                            } else if ("rectangle".equals(type)) {
                                Rectangle2D.Float rectangle = new Rectangle2D.Float(
                                        ((Number) shapeObj.get("x")).floatValue(),
                                        ((Number) shapeObj.get("y")).floatValue(),
                                        ((Number) shapeObj.get("width")).floatValue(),
                                        ((Number) shapeObj.get("height")).floatValue()
                                );
                                shapes.add(rectangle);
                            } else {
                                System.out.println("Unexpected shape type: " + shape.getClass().getName()); // Add debug statement
                            }
                        }

                        for (Object color : colorsJson) {
                            colors.add(new Color(((Long) color).intValue()));
                        }

                        for (Object position : shapePositionsJson) {
                            JSONObject positionJson = (JSONObject) position;
                            int x = ((Long) positionJson.get("x")).intValue();
                            int y = ((Long) positionJson.get("y")).intValue();
                            shapePositions.add(new Point(x, y));
                        }
                    }
                    System.out.println("Parsed from file");
                    for (ClientInterface client : clients) {
                        try {
                            client.clear();
                            client.updateBoardStatus(shapes, colors, shapePositions);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("update to client");
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            } else {
                // Show an error message or handle the null file case
                System.err.println("No file was selected.");
            }
        }
    }

    public void fileSelect(String option) {
        switch (option) {
            case "Save":
                save(null);
                break;
            case "Save As":
                saveAs();
                break;
            case "Open":
                open();
                break;
            case "New":
                shapes.clear();
                colors.clear();
                shapePositions.clear();
                for (ClientInterface client : clients) {
                    try {
                        client.clear();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "Close":
                closeServer();
                break;
        }
    }
}
