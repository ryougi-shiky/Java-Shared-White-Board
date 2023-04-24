import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    private javax.swing.JFrame frame;
    private javax.swing.JList<String> clientList;
    private javax.swing.DefaultListModel<String> clientListModel;

    public ServerGUI() {
        frame = new javax.swing.JFrame("Shared Board White Server");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new java.awt.BorderLayout());

        clientListModel = new javax.swing.DefaultListModel<>();
        clientList = new javax.swing.JList<>(clientListModel);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(clientList);
        frame.add(scrollPane, java.awt.BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public void addClient(String clientName) {
        clientListModel.addElement(clientName);
    }

    public void removeClient(String clientName) {
        clientListModel.removeElement(clientName);
    }
}