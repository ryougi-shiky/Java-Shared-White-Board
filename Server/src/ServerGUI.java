import javax.swing.*;
import java.awt.*;
import javax.swing.JScrollPane;
import java.awt.event.*;
import java.rmi.RemoteException;

public class ServerGUI {
    private ServerInterface server;
    private JFrame frame;
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private static int serverPortNumber;
    public ServerGUI() {
        frame = new JFrame("Shared Board White Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 500);
        frame.setLayout(new BorderLayout());

        frame.add(new JLabel(" Connected Clients List: "), BorderLayout.NORTH);

        // Add the server info panel
        JPanel serverInfoPanel = new JPanel(new GridLayout(2,1));
        serverInfoPanel.add(new JLabel(" Server Address: 127.0.0.1 "));
        serverInfoPanel.add(new JLabel(" Port: " + serverPortNumber));
        frame.add(serverInfoPanel, BorderLayout.SOUTH);

        // Display the joined clients list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        JScrollPane scrollPane = new JScrollPane(clientList);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                try {
                    server.closeServer();
                    System.out.println("Server notify server remote object to every client to close window");
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    // Initialise the setting of the server.
    // When open the program, pop up a window to enter the port number that the server will run on
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
                    serverPortNumber = portNumber;
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

    public void setServerInterface(ServerInterface server){
        this.server = server;
    }
}