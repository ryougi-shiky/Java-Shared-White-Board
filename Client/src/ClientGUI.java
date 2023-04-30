import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.awt.geom.*;
import java.rmi.RemoteException;

public class ClientGUI extends JFrame {
    private ClientInterface client;
    private ServerInterface server;
    private whiteBoard whiteBoard;
    private static Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
            Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW,
            new Color(147, 112, 219), new Color(50, 205, 50), new Color(0, 191, 255), new Color(139, 69, 19)};
    // The last four colours are Medium Purple, Lime Green, Deep Sky Blue, Saddle Brown

    public ClientGUI(ServerInterface server, ClientInterface client) {
        this.server = server;
        this.client = client;

        setTitle("Shared Whiteboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);

        TopBarMenu();
        drawBoard();
    }

    public static String[] login() {
        JTextField usernameField = new JTextField(15);
        JTextField serverAddressField = new JTextField("127.0.0.1", 15);
        JTextField portNumberField = new JTextField("50000", 15);

        JPanel loginPanel = new JPanel(new GridLayout(0, 1));
        loginPanel.add(new JLabel("Welcome to shared white board!"));
        loginPanel.add(new JLabel("Please join a server:"));
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Server Address:"));
        loginPanel.add(serverAddressField);
        loginPanel.add(new JLabel("Port Number:"));
        loginPanel.add(portNumberField);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Join a Server", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{usernameField.getText(), serverAddressField.getText(), portNumberField.getText()};
        } else {
            System.exit(0);
            return null;
        }
    }

    public static void loginError(String err) {
        JOptionPane.showMessageDialog(null, err, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void TopBarMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu shapesMenu = new JMenu("Shapes");
        String[] shapes = {"Line", "Circle", "Oval", "Rectangle"};
        for (String shape : shapes) {
            JMenuItem menuItem = new JMenuItem(shape);
            menuItem.addActionListener(e -> whiteBoard.setShape(shape));
            shapesMenu.add(menuItem);
        }
        menuBar.add(shapesMenu);

        JMenu colorsMenu = new JMenu("Colors");
        for (Color color : colors) {
            JMenuItem menuItem = new JMenuItem(new ImageIcon(createColorIcon(color)));
            menuItem.addActionListener(e -> whiteBoard.setColor(color));
            colorsMenu.add(menuItem);
        }
        menuBar.add(colorsMenu);

        JMenu textMenu = new JMenu("Text");
        JMenuItem menuItem = new JMenuItem("Add Text Box");
        menuItem.addActionListener(e -> whiteBoard.createTextBox());
        textMenu.add(menuItem);
        menuBar.add(textMenu);

        setJMenuBar(menuBar);
    }

    private Image createColorIcon(Color color) {
        BufferedImage colorIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D board = colorIcon.createGraphics();
        board.setColor(color);
        board.fillRect(0, 0, 16, 16);
        board.dispose();
        return colorIcon;
    }

    private void drawBoard() {
        whiteBoard = new whiteBoard();
        getContentPane().add(whiteBoard, BorderLayout.CENTER);
    }

    class whiteBoard extends JPanel {
        private Shape currentDrawing; // The shape is currently drawing. If null, currently not drawing.
        private Color currentColor = Color.BLACK;
        private String currentShape = "Line";
        private int x, y; // Mouse position
        private int width, height; // Used for drawing shapes. Computed by mouse position
        // store drawn shapes , positions and corresponding colours
        private ArrayList<Object> shapes;
        private ArrayList<Color> colors;
        private ArrayList<Point> shapePositions;
        // Temporarily draw partial shapes
        private Shape lastPartialShape;
        private Color lastPartialColor;
        private String lastPartialShapeType;
        private JTextField textBox;
        private boolean textBoxEnabled = false;

        public whiteBoard() {
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);

            shapes = new ArrayList<>();
            colors = new ArrayList<>();
            shapePositions = new ArrayList<>();

            lastPartialShape = null;
            lastPartialColor = null;
            lastPartialShapeType = null;


            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    x = e.getX();
                    y = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    clearPartialShapes();
                    drawShape(e);
                    shapes.add(currentDrawing);
                    colors.add(currentColor);
                    shapePositions.add(new Point(x, y));
                    currentDrawing = null;
                    repaint();
                    drawToServer();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    drawShape(e);
                    drawPartialToServer(currentDrawing, currentColor, currentShape);
                    repaint();
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);


            textBox = new JTextField();
            textBox.setBounds(0, 0, 100, 20);
            textBox.setVisible(false);
            textBox.addActionListener(e -> {
                textBox.setVisible(false);
                textBoxEnabled = false;
                // Only store the text string and its position, not the text box object itself
                shapes.add(textBox.getText());
                shapePositions.add(new Point(textBox.getX(), textBox.getY() + textBox.getHeight()));
                colors.add(currentColor);
                textBox.setText("");
                repaint();
                drawToServer();
            });
            add(textBox);
        }

        // add a partial shape
        public void addPartialShape(Shape curDrawing, Color curColor, String curShape) {
            lastPartialShape = curDrawing;
            lastPartialColor = curColor;
            lastPartialShapeType = curShape;
            repaint();
        }

        // Display partial draw from other clients
        public void drawPartialShape(int startX, int startY, int curX, int curY, Shape curDrawing, Color curColor, String curShape) {
            int tmpWidth = width;
            width = curX - startX;
            int tmpHeight = height;
            height = curY - startY;
            Shape tmpCurrentDrawing = currentDrawing;
            Color tmpCurrentColor = currentColor;
            String tmpShape = currentShape;
            currentDrawing = curDrawing;
            currentColor = curColor;
            currentShape = curShape;
            System.out.println("currentDrawing: " + currentDrawing + "  currentColor: " + currentColor);
            repaint();
            System.out.println(" Partial drew");
            width = tmpWidth;
            height = tmpHeight;
            currentDrawing = tmpCurrentDrawing;
            currentColor = tmpCurrentColor;
            currentShape = tmpShape;
        }

        // clear partial shapes after mouse released
        public void clearPartialShapes() {
            lastPartialShape = null;
            lastPartialColor = null;
            lastPartialShapeType = null;
            repaint();
        }


        private void drawShape(MouseEvent e) {
            width = e.getX() - x;
            height = e.getY() - y;

            switch (currentShape) {
                case "Line":
                    currentDrawing = new Line2D.Float(x, y, e.getX(), e.getY());
                    break;
                case "Circle":
                    int diameter = Math.max(Math.abs(width), Math.abs(height));
                    currentDrawing = new Ellipse2D.Float(
                            x - (width < 0 ? diameter : 0),
                            y - (height < 0 ? diameter : 0),
                            diameter, diameter);
                    break;
                case "Oval":
                    currentDrawing = new Ellipse2D.Float(
                            x - (width < 0 ? -width : 0),
                            y - (height < 0 ? -height : 0),
                            Math.abs(width), Math.abs(height));
                    break;
                case "Rectangle":
                    currentDrawing = new Rectangle(
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
        protected void paintComponent(Graphics board) {
            // Ensure the original painting operations are executed
            super.paintComponent(board);
            Graphics2D board2D = (Graphics2D) board;

            // Draw the all shapes and text boxes
            for (int i = 0; i < shapes.size(); i++) {
                board2D.setColor(colors.get(i));
                if (shapes.get(i) instanceof Shape) {
                    board2D.draw((Shape) shapes.get(i));
                } else if (shapes.get(i) instanceof String) {
                    board2D.drawString((String) shapes.get(i), (int) shapePositions.get(i).getX(), (int) shapePositions.get(i).getY());
                }
            }

            // Draw the partial shapes
            if (lastPartialShape != null) {
                board2D.setColor(lastPartialColor);
                board2D.draw(lastPartialShape);
            }

            // Display the current drawing shape
            if (currentDrawing != null) {
                board2D.setColor(currentColor);
                board2D.draw(currentDrawing);
            }
        }

        private void drawToServer(){
            try {
                server.draw(client);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        private void drawPartialToServer(Shape curDrawing, Color curColor, String curShape){
            try {
                server.partialDraw(client, curDrawing, curColor, curShape);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        public void createTextBox() {
            textBoxEnabled = true;
        }

        // Text box will follow the mouse motion
        @Override
        protected void processMouseMotionEvent(MouseEvent e) {
            super.processMouseMotionEvent(e);
            if (textBoxEnabled) {
                textBox.setLocation(e.getX(), e.getY());
                textBox.setVisible(true);
            }
        }

        public void setShape(String shape) {
            currentShape = shape;
        }

        public void setColor(Color color) {
            currentColor = color;
        }


    }
    // Used to send current board status to the server
    public List<List<?>> getBoardStatus() {
        List<List<?>> boardStatus = new ArrayList<>();
        boardStatus.add(whiteBoard.shapes);
        boardStatus.add(whiteBoard.colors);
        boardStatus.add(whiteBoard.shapePositions);
        return boardStatus;
    }

    // Get sync from server
    public void updateBoardStatus(ArrayList<Object> shapes, ArrayList<Color> colors, ArrayList<Point> shapePositions) {
        this.whiteBoard.shapes = shapes;
        this.whiteBoard.colors = colors;
        this.whiteBoard.shapePositions = shapePositions;
        this.whiteBoard.repaint();
    }

    public void updatePartialDraw(Shape curDrawing, Color curColor, String curShape){
        this.whiteBoard.addPartialShape(curDrawing, curColor, curShape);
    }
}
