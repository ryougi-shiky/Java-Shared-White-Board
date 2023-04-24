import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.geom.*;

public class ClientGUI extends JFrame {
    private whiteBoard setWhiteBoard;
    private static Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
            Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW,
            new Color(147, 112, 219), new Color(50, 205, 50), new Color(0, 191, 255), new Color(139, 69, 19)};
            // Medium Purple, Lime Green, Deep Sky Blue, Saddle Brown

    public ClientGUI() {
        setTitle("Shared Whiteboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);

        TopBarMenu();
        drawBoard();
    }

    private void TopBarMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu shapesMenu = new JMenu("Shapes");
        String[] shapes = {"Line", "Circle", "Oval", "Rectangle"};
        for (String shape : shapes) {
            JMenuItem menuItem = new JMenuItem(shape);
            menuItem.addActionListener(e -> setWhiteBoard.setShape(shape));
            shapesMenu.add(menuItem);
        }
        menuBar.add(shapesMenu);

        JMenu colorsMenu = new JMenu("Colors");
        for (Color color : colors) {
            JMenuItem menuItem = new JMenuItem(new ImageIcon(createColorIcon(color)));
            menuItem.addActionListener(e -> setWhiteBoard.setColor(color));
            colorsMenu.add(menuItem);
        }
        menuBar.add(colorsMenu);

        setJMenuBar(menuBar);
    }

    private Image createColorIcon(Color color) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, 16, 16);
        g2d.dispose();
        return image;
    }

    private void drawBoard() {
        setWhiteBoard = new whiteBoard();
        getContentPane().add(setWhiteBoard, BorderLayout.CENTER);
    }

    class whiteBoard extends JPanel {

        private Shape currentDrawing;
        private Color currentColor = Color.BLACK;
        private String currentShape = "Line";
        private int x, y, width, height;
        private ArrayList<java.awt.Shape> shapes;
        private ArrayList<java.awt.Color> colors;

        public whiteBoard() {
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);

            shapes = new ArrayList<>();
            colors = new ArrayList<>();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    x = e.getX();
                    y = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    createShape(e);
                    shapes.add(currentDrawing);
                    colors.add(currentColor);
                    currentDrawing = null;
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    createShape(e);
                    repaint();
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        private void createShape(MouseEvent e) {
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
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (int i = 0; i < shapes.size(); i++) {
                g2d.setColor(colors.get(i));
                g2d.draw(shapes.get(i));
            }

            if (currentDrawing != null) {
                g2d.setColor(currentColor);
                g2d.draw(currentDrawing);
            }
        }
        private void setShape(String shape) {
            currentShape = shape;
        }

        public void setColor(Color color) {
            currentColor = color;
        }

    }
}
