public class ClientGUI extends javax.swing.JFrame {
    private ClientInterface client;
    private ServerInterface server;
    private ClientGUI.whiteBoard whiteBoard;
    private static java.awt.Color[] colors = {java.awt.Color.BLACK, java.awt.Color.BLUE, java.awt.Color.CYAN, java.awt.Color.DARK_GRAY, java.awt.Color.GRAY, java.awt.Color.GREEN,
            java.awt.Color.LIGHT_GRAY, java.awt.Color.MAGENTA, java.awt.Color.ORANGE, java.awt.Color.PINK, java.awt.Color.RED, java.awt.Color.YELLOW,
            new java.awt.Color(147, 112, 219), new java.awt.Color(50, 205, 50), new java.awt.Color(0, 191, 255), new java.awt.Color(139, 69, 19)};
    // The last four colours are Medium Purple, Lime Green, Deep Sky Blue, Saddle Brown

    // List of other users currently editing the board
    private javax.swing.DefaultListModel<String> clientsListModel;
    private javax.swing.JList<String> clientsList;

    public ClientGUI(ServerInterface server, ClientInterface client) {
        this.server = server;
        this.client = client;

        setTitle("Shared Whiteboard");
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);

        TopBarMenu();
        drawBoard();
        rightPanel();
        bottomPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override // Close window, disconnect the whiteboard
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    server.leave(client); // Notify the server to remove this client
                } catch (java.rmi.RemoteException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    public static String[] login() {
        javax.swing.JTextField usernameField = new javax.swing.JTextField(15);
        javax.swing.JTextField serverAddressField = new javax.swing.JTextField("127.0.0.1", 15);
        javax.swing.JTextField portNumberField = new javax.swing.JTextField("50000", 15);

        javax.swing.JPanel loginPanel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1));
        loginPanel.add(new javax.swing.JLabel("Welcome to shared white board!"));
        loginPanel.add(new javax.swing.JLabel("Please join a server:"));
        loginPanel.add(new javax.swing.JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new javax.swing.JLabel("Server Address:"));
        loginPanel.add(serverAddressField);
        loginPanel.add(new javax.swing.JLabel("Port Number:"));
        loginPanel.add(portNumberField);

        int result = javax.swing.JOptionPane.showConfirmDialog(null, loginPanel, "Join a Server", javax.swing.JOptionPane.OK_CANCEL_OPTION);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            return new String[]{usernameField.getText(), serverAddressField.getText(), portNumberField.getText()};
        } else {
            System.exit(0);
            return null;
        }
    }

    public static void loginError(String err) {
        javax.swing.JOptionPane.showMessageDialog(null, err, "Login Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    private void TopBarMenu() {
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

        javax.swing.JMenu shapesMenu = new javax.swing.JMenu("Shapes");
        String[] shapes = {"Line", "Circle", "Oval", "Rectangle"};
        for (String shape : shapes) {
            javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem(shape);
            menuItem.addActionListener(e -> whiteBoard.setShape(shape));
            shapesMenu.add(menuItem);
        }
        menuBar.add(shapesMenu);

        javax.swing.JMenu colorsMenu = new javax.swing.JMenu("Colors");
        for (java.awt.Color color : colors) {
            javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem(new javax.swing.ImageIcon(createColorIcon(color)));
            menuItem.addActionListener(e -> whiteBoard.setColor(color));
            colorsMenu.add(menuItem);
        }
        menuBar.add(colorsMenu);

        javax.swing.JMenu textMenu = new javax.swing.JMenu("Text");
        javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem("Add Text Box");
        menuItem.addActionListener(e -> whiteBoard.enableTextBox());
        textMenu.add(menuItem);
        menuBar.add(textMenu);

        setJMenuBar(menuBar);
    }

    private java.awt.Image createColorIcon(java.awt.Color color) {
        java.awt.image.BufferedImage colorIcon = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D board = colorIcon.createGraphics();
        board.setColor(color);
        board.fillRect(0, 0, 16, 16);
        board.dispose();
        return colorIcon;
    }

    private void drawBoard() {
        whiteBoard = new ClientGUI.whiteBoard();
        getContentPane().add(whiteBoard, java.awt.BorderLayout.CENTER);
    }

    private void rightPanel() {
        clientsListModel = new javax.swing.DefaultListModel<>();
        clientsList = new javax.swing.JList<>(clientsListModel);

        javax.swing.JScrollPane userListScrollPane = new javax.swing.JScrollPane(clientsList);
        userListScrollPane.setPreferredSize(new java.awt.Dimension(200, 0));

        javax.swing.JPanel userListPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        userListPanel.add(new javax.swing.JLabel("Online Clients"), java.awt.BorderLayout.NORTH);
        userListPanel.add(userListScrollPane, java.awt.BorderLayout.CENTER);

        add(userListPanel, java.awt.BorderLayout.EAST);
    }

    private void bottomPanel() {
        javax.swing.JPanel serverInfoPanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 1));
        try {
            serverInfoPanel.add(new javax.swing.JLabel(" Your name: " + client.getClientName()));
        } catch (java.rmi.RemoteException e) {
            e.printStackTrace();
        }
        add(serverInfoPanel, java.awt.BorderLayout.SOUTH);
    }

    public void updateClientsList(java.util.List<String> clientList) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.DefaultListModel<String> listModel = new javax.swing.DefaultListModel<>();
            for (String clientName : clientList) {
                try { // Only show other online users
                    if (!clientName.equals(client.getClientName())) {
                        listModel.addElement(clientName);
                    }
                } catch (java.rmi.RemoteException e) {
                    e.printStackTrace();
                }
            }
            clientsList.setModel(listModel);
        });
    }

    class whiteBoard extends javax.swing.JPanel {
        private java.awt.Shape currentDrawing; // The shape is currently drawing. If null, currently not drawing.
        private java.awt.Color currentColor = java.awt.Color.BLACK;
        private String currentShape = "Line";
        private int x, y; // Mouse position
        private int width, height; // Used for drawing shapes. Computed by mouse position
        // store drawn shapes , positions and corresponding colours
        private java.util.ArrayList<Object> shapes;
        private java.util.ArrayList<java.awt.Color> colors;
        private java.util.ArrayList<java.awt.Point> shapePositions;
        // Temporarily draw partial shapes
        private java.awt.Shape syncPartialDrawing;
        private java.awt.Color syncPartialColor;
        private String syncPartialShape;
        private javax.swing.JTextField textBox;
        private boolean textBoxEnabled = false;

        public whiteBoard() {
            setPreferredSize(new java.awt.Dimension(800, 600));
            setBackground(java.awt.Color.WHITE);

            shapes = new java.util.ArrayList<>();
            colors = new java.util.ArrayList<>();
            shapePositions = new java.util.ArrayList<>();

            syncPartialDrawing = null;
            syncPartialColor = null;
            syncPartialShape = null;

            java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    x = e.getX();
                    y = e.getY();
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    clearPartialShapes();
                    drawShape(e);
                    shapes.add(currentDrawing);
                    colors.add(currentColor);
                    shapePositions.add(new java.awt.Point(x, y));
                    currentDrawing = null;
                    repaint();
                    drawToServer();
                }

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    drawShape(e);
                    drawPartialToServer(currentDrawing, currentColor, currentShape);
                    repaint();
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);


            textBox = new javax.swing.JTextField();
            textBox.setBounds(0, 0, 100, 20);
            textBox.setVisible(false); // Hide text box until user need to use text box
            textBox.addActionListener(e -> {
                textBox.setVisible(false); // Finish enter text, set text box back to invisible
                textBoxEnabled = false;
                // Calculate the baseline position for the text box
                java.awt.FontMetrics fm = textBox.getFontMetrics(textBox.getFont());

                // Only store the text string and its position, not the text box object itself
                shapes.add(textBox.getText());
                shapePositions.add(new java.awt.Point(textBox.getX(), (textBox.getY() + fm.getAscent())));
                colors.add(currentColor);
                textBox.setText("");
                repaint();
                drawToServer();
            });
            add(textBox);
        }

        // add a partial shape
        public void addPartialShape(java.awt.Shape curDrawing, java.awt.Color curColor, String curShape) {
            syncPartialDrawing = curDrawing;
            syncPartialColor = curColor;
            syncPartialShape = curShape;

            repaint();
        }

        // clear partial shapes after mouse released
        public void clearPartialShapes() {
            syncPartialDrawing = null;
            syncPartialColor = null;
            syncPartialShape = null;
            repaint();
        }


        private void drawShape(java.awt.event.MouseEvent e) {
            width = e.getX() - x;
            height = e.getY() - y;

            switch (currentShape) {
                case "Line":
                    currentDrawing = new java.awt.geom.Line2D.Float(x, y, e.getX(), e.getY());
                    break;
                case "Circle":
                    int diameter = Math.max(Math.abs(width), Math.abs(height));
                    currentDrawing = new java.awt.geom.Ellipse2D.Float(
                            x - (width < 0 ? diameter : 0),
                            y - (height < 0 ? diameter : 0),
                            diameter, diameter);
                    break;
                case "Oval":
                    currentDrawing = new java.awt.geom.Ellipse2D.Float(
                            x - (width < 0 ? -width : 0),
                            y - (height < 0 ? -height : 0),
                            Math.abs(width), Math.abs(height));
                    break;
                case "Rectangle":
                    currentDrawing = new java.awt.Rectangle(
                            x - (width < 0 ? -width : 0),
                            y - (height < 0 ? -height : 0),
                            Math.abs(width), Math.abs(height));
                    break;
                case "Text":
                    if (textBoxEnabled) {
                        textBox.setLocation(e.getX(), e.getY());
                        textBox.setVisible(true);
                        textBox.requestFocus();
                    }
                    break;
            }
        }

        // paintComponent is called when invoking repaint()
        @Override
        protected void paintComponent(java.awt.Graphics board) {
            // Ensure the original painting operations are executed
            super.paintComponent(board);
            java.awt.Graphics2D board2D = (java.awt.Graphics2D) board;

            // Draw the all shapes and text boxes
            for (int i = 0; i < shapes.size(); i++) {
                board2D.setColor(colors.get(i));
                if (shapes.get(i) instanceof java.awt.Shape) {
                    board2D.draw((java.awt.Shape) shapes.get(i));
                } else if (shapes.get(i) instanceof String) { // If it's text box
                    board2D.drawString((String) shapes.get(i), (int) shapePositions.get(i).getX(), (int) shapePositions.get(i).getY());
                }
            }

            // Draw the partial shapes
            if (syncPartialDrawing != null) {
                board2D.setColor(syncPartialColor);
                board2D.draw(syncPartialDrawing);
            }

            // Display the current drawing shape
            if (currentDrawing != null) {
                board2D.setColor(currentColor);
                board2D.draw(currentDrawing);
            }
        }

        private void drawToServer() {
            try {
                server.draw(client);
            } catch (java.rmi.RemoteException e) {
                e.printStackTrace();
            }
        }

        private void drawPartialToServer(java.awt.Shape curDrawing, java.awt.Color curColor, String curShape) {
            try {
                server.partialDraw(client, curDrawing, curColor, curShape);
            } catch (java.rmi.RemoteException e) {
                e.printStackTrace();
            }
        }

        public void enableTextBox() {
            textBoxEnabled = true;
        }

        // Text box will follow the mouse motion
        @Override
        protected void processMouseMotionEvent(java.awt.event.MouseEvent e) {
            super.processMouseMotionEvent(e);
            if (textBoxEnabled) {
                textBox.setLocation(e.getX(), e.getY());
                textBox.setVisible(true);
            }
        }

        public void setShape(String shape) {
            currentShape = shape;
        }

        public void setColor(java.awt.Color color) {
            currentColor = color;
        }

    }

    // Used to send current board status to the server
    public java.util.List<java.util.List<?>> getBoardStatus() {
        java.util.List<java.util.List<?>> boardStatus = new java.util.ArrayList<>();
        boardStatus.add(whiteBoard.shapes);
        boardStatus.add(whiteBoard.colors);
        boardStatus.add(whiteBoard.shapePositions);
        return boardStatus;
    }

    // Get sync from server
    public void updateBoardStatus(java.util.ArrayList<Object> shapes, java.util.ArrayList<java.awt.Color> colors, java.util.ArrayList<java.awt.Point> shapePositions) {
        this.whiteBoard.shapes = shapes;
        this.whiteBoard.colors = colors;
        this.whiteBoard.shapePositions = shapePositions;
        this.whiteBoard.repaint();
    }

    public void updatePartialDraw(java.awt.Shape curDrawing, java.awt.Color curColor, String curShape) {
        this.whiteBoard.addPartialShape(curDrawing, curColor, curShape);
    }

    public void closeByServer() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Notification", false);
        dialog.setSize(250, 100);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new javax.swing.BoxLayout(dialog.getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        dialog.getContentPane().add(new javax.swing.JLabel(" The server has closed the connection."));
        dialog.getContentPane().add(new javax.swing.JLabel(" Please close the window and retry."));
        dialog.setVisible(true);
        System.out.println("client GUI Closed client's windows");
    }

    public void kicked() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Notification", false);
        dialog.setSize(250, 100);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new javax.swing.BoxLayout(dialog.getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        dialog.getContentPane().add(new javax.swing.JLabel(" The server kicked you out."));
        dialog.getContentPane().add(new javax.swing.JLabel(" Please close the window and retry."));
        dialog.setVisible(true);
        client = null; // Disconnect
        server = null;
        System.out.println("client is kicked out");
//        System.exit(0);
    }

    public void clear() {
        // Reset all values
        this.whiteBoard.shapes.clear();
        this.whiteBoard.colors.clear();
        this.whiteBoard.shapePositions.clear();

        this.whiteBoard.syncPartialDrawing = null;
        this.whiteBoard.syncPartialColor = null;
        this.whiteBoard.syncPartialShape = null;

        this.whiteBoard.currentDrawing = null;
        this.whiteBoard.currentColor = java.awt.Color.BLACK;
        this.whiteBoard.currentShape = "Line";

        this.whiteBoard.repaint();

        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Notification", false);
        dialog.setSize(250, 100);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new javax.swing.BoxLayout(dialog.getContentPane(), javax.swing.BoxLayout.Y_AXIS));
        dialog.getContentPane().add(new javax.swing.JLabel(" The server open or create a new board."));
        dialog.getContentPane().add(new javax.swing.JLabel(" This is a new board now."));
        dialog.setVisible(true);
    }
}
