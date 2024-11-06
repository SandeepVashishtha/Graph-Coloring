import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InteractiveGraphColoringGUI extends JPanel {

    private int V;
    private LinkedList<Integer>[] adjList;
    private int[] colors;
    private static final int RADIUS = 20;
    private Point[] positions;
    private int selectedVertex = -1;

    public InteractiveGraphColoringGUI(int V) {
        this.V = V;
        adjList = new LinkedList[V];
        for (int i = 0; i < V; i++) {
            adjList[i] = new LinkedList<>();
        }
        colors = new int[V];
        Arrays.fill(colors, -1);
        positions = new Point[V];

        Random rand = new Random();
        for (int i = 0; i < V; i++) {
            positions[i] = new Point(rand.nextInt(500) + 50, rand.nextInt(400) + 50);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (int i = 0; i < V; i++) {
                    if (positions[i].distance(e.getPoint()) < RADIUS) {
                        selectedVertex = i;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = -1;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedVertex != -1) {
                    positions[selectedVertex] = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            int u = -1;

            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < V; i++) {
                    if (positions[i].distance(e.getPoint()) < RADIUS) {
                        if (u == -1) {
                            u = i;
                        } else {
                            addEdge(u, i);
                            u = -1;
                            repaint();
                        }
                        break;
                    }
                }
            }
        });
    }

    public void addEdge(int u, int v) {
        if (u != v && !adjList[u].contains(v)) {
            adjList[u].add(v);
            adjList[v].add(u);
        }
    }

    public void greedyColoring() {
        colors[0] = 0;

        boolean[] availableColors = new boolean[V];
        Arrays.fill(availableColors, true);

        for (int u = 1; u < V; u++) {
            for (int i : adjList[u]) {
                if (colors[i] != -1) {
                    availableColors[colors[i]] = false;
                }
            }

            int color;
            for (color = 0; color < V; color++) {
                if (availableColors[color]) {
                    break;
                }
            }

            colors[u] = color;
            Arrays.fill(availableColors, true);
        }

        int maxColor = Arrays.stream(colors).max().orElse(-1) + 1;
        JOptionPane.showMessageDialog(this, "Minimum number of colors required: " + maxColor);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        for (int u = 0; u < V; u++) {
            for (int v : adjList[u]) {
                g.drawLine(positions[u].x, positions[u].y, positions[v].x, positions[v].y);
            }
        }

        for (int u = 0; u < V; u++) {
            g.setColor(getColor(colors[u]));
            g.fillOval(positions[u].x - RADIUS / 2, positions[u].y - RADIUS / 2, RADIUS, RADIUS);
            g.setColor(Color.BLACK);
            g.drawString("V" + u, positions[u].x - 10, positions[u].y - RADIUS / 2 - 5);
        }
    }

    private Color getColor(int colorIndex) {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE};
        return colors[colorIndex % colors.length];
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Interactive Graph Coloring");

        String vertexInput = JOptionPane.showInputDialog("Enter number of vertices:");
        int numVertices = Integer.parseInt(vertexInput);

        InteractiveGraphColoringGUI graph = new InteractiveGraphColoringGUI(numVertices);

        JButton colorButton = new JButton("Color Graph");
        colorButton.addActionListener(e -> graph.greedyColoring());

        frame.setLayout(new BorderLayout());
        frame.add(graph, BorderLayout.CENTER);
        frame.add(colorButton, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }
}
