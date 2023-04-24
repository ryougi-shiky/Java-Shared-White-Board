import javax.swing.*;
import java.awt.*;
import javax.swing.JScrollPane;

public class ServerGUI {
    private JFrame frame;
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;

    public ServerGUI() {
        frame = new JFrame("Shared Board White Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 500);
        frame.setLayout(new BorderLayout());

        // Display the joined clients list
        clientListModel = new javax.swing.DefaultListModel<>();
        clientList = new javax.swing.JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientList);
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