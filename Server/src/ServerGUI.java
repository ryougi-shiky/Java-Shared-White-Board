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

    public static int setup() {
        JTextField portNumberField = new JTextField("50000", 15);

        JPanel serverStartPanel = new JPanel(new GridLayout(0, 1));
        serverStartPanel.add(new JLabel("Welcome to shared white board!"));
        serverStartPanel.add(new JLabel("Please set the server port (49152-65535):"));
        serverStartPanel.add(portNumberField);
        int portNumber;
        while (true){
            int result = JOptionPane.showConfirmDialog(null, serverStartPanel, "Join a Server", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION){
                portNumber = Integer.parseInt(portNumberField.getText());
                if (portNumber < 49152 || portNumber > 65535){
                    JOptionPane.showMessageDialog(null, "Please enter a port number 49152 - 65535!",
                            "Invalid Port Number", JOptionPane.ERROR_MESSAGE);
                } else {
                    return portNumber;
                }
            } else {
                System.exit(0);
                return 0;
            }

        }
    }

    public void addClient(String clientName) {
        clientListModel.addElement(clientName);
    }

    public void removeClient(String clientName) {
        clientListModel.removeElement(clientName);
    }
}