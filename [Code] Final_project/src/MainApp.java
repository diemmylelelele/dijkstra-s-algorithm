import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;

class GraphPanel extends JPanel {
    public static int vertexRadius = 16;
    public static int marginX = 25;
    public static int marginY = 25;
    public static float xFactor, yFactor;
    public Dijkstra dijkstra;
    public HashMap<String, List<Edge>> overlayEdges;

    public GraphPanel(Dijkstra dijkstra) {
        this.dijkstra = dijkstra;
        overlayEdges = new HashMap<>();
        overlayEdges.put("weighted", new LinkedList<>());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int minX = 0;
        int minY = 0;
        int maxX = 1;
        int maxY = 1;

        for (Vertex v : dijkstra.getVertices()) {
            if (v.x < minX) minX = v.x;
            if (v.x > maxX) maxX = v.x;
            if (v.y < minY) minY = v.y;
            if (v.y > maxY) maxY = v.y;
        }

        xFactor = (getWidth() - 2 * marginX) / (maxX - minX);
        yFactor = (getHeight() - 2 * marginY) / (maxY - minY);

        paintGraph(g);
    }

    private void paintGraph(Graphics g) {
        for (Vertex v : dijkstra.getVertices()) {
            for (Edge edge : v.adjacentEdges) {
                paintEdge(g, edge.start, edge.end, edge.distance, new Color(204, 229, 255), 2, 255);
            }
        }

        for (Vertex v : dijkstra.getVertices()) {
            paintVertex(g, v, new Color(0, 128, 255));
        }

        for (String key : overlayEdges.keySet()) {
            if (key.equals("weighted")) {
                for (Edge edge : overlayEdges.get(key)) {
                    paintEdge(g, edge.start, edge.end, edge.distance, new Color(51, 51, 255), 3, 100);
                }
            }
        }
    }

    private void paintVertex(Graphics g, Vertex v, Color color) {
        int x = Math.round(xFactor * v.x + marginX);
        int y = Math.round(yFactor * v.y + marginY);

        g.setColor(color);
        g.fillOval(x - vertexRadius / 2, y - vertexRadius / 2, vertexRadius, vertexRadius);
        g.drawString(v.Name, x - v.Name.length()*2-35, y + vertexRadius - 29);
    }

    private void paintEdge(Graphics g, Vertex start, Vertex end, double weight, Color color, int thickness, int alpha) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));

        int startX = Math.round(xFactor * start.x + marginX);
        int startY = Math.round(yFactor * start.y + marginY);
        int endX = Math.round(xFactor * end.x + marginX);
        int endY = Math.round(yFactor * end.y + marginY);

        g2.drawLine(startX, startY, endX, endY);

        g2.setColor(Color.BLACK);
        g2.drawString(String.format("%.1f", weight), (startX + endX) / 2, (startY + endY) / 2);
    }
}
class GraphUtils {
    public static Dijkstra readGraph(String vertexFile, String edgeFile) {
        Dijkstra dijkstra = new Dijkstra();
        try {
            InputStream inputStream = Dijkstra.class.getResourceAsStream("/names.csv");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(streamReader);
            String line = br.readLine();
            while (line != null) {
                String[] vertex = line.split(",");
                if (vertex.length >= 3) {
                    String Name = vertex[0];
                    int x = Integer.parseInt(vertex[1]);
                    int y = Integer.parseInt(vertex[2]);
                    Vertex v = new Vertex(Name, x, y);
                    dijkstra.addVertex(v);
                } else {
                    System.err.println("Invalid data format in CSV file");
                }
                line = br.readLine();
            }
            br.close();

            InputStream inputStream1 = Dijkstra.class.getResourceAsStream("/pair.csv");
            InputStreamReader streamReader1 = new InputStreamReader(inputStream1);
            BufferedReader edge_br = new BufferedReader(streamReader1);
            String edge_line = edge_br.readLine();
            while (edge_line != null) {
                String[] edge = edge_line.split(",");
                dijkstra.addUndirectedEdge(edge[0], edge[1], Double.parseDouble(edge[2]));
                edge_line = edge_br.readLine();
            }
            edge_br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dijkstra;
    }
}

public class MainApp extends JFrame {
    private JPanel contentPanel;
    private Dijkstra dijkstra;
    private JComboBox<String> startComboBox;
    private JComboBox<String> endComboBox;
    private GraphPanel graphPanel;

    public MainApp() {
        initializeUI();
        updateGraphPanel();
    }

    private void initializeUI() {
        this.setTitle("Shortest Path Visualization");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.cyan);
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setContentPane(contentPanel);
        // Read the graph from the CSV files
        dijkstra = GraphUtils.readGraph("./names.csv", "./pair.csv");

        // Create the control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        startComboBox = new JComboBox<>();
        startComboBox.setPreferredSize(new Dimension(150, 30)); // Set the size of the startComboBox

        endComboBox = new JComboBox<>();
        endComboBox.setPreferredSize(new Dimension(150, 30)); // Set the size of the endComboBox

        controlPanel.add(new JLabel("Start:"));
        controlPanel.add(startComboBox);
        controlPanel.add(new JLabel("End:"));
        controlPanel.add(endComboBox);

        // Create dijkstraButton to show the shortest path
        JButton dijkstraButton = new JButton("Draw Dijkstra's Path");
        dijkstraButton.setForeground(Color.BLUE);
        dijkstraButton.addActionListener(new DijkstraButtonListener());
        controlPanel.add(dijkstraButton);

        // Reset button to reset the graph
        JButton resetButton = new JButton("Reset");
        resetButton.setForeground(Color.BLUE);
        resetButton.addActionListener(new ResetButtonListener());
        controlPanel.add(resetButton);
        // Add the control panel to the content panel
        // 
        controlPanel.setBackground( new Color(51,153, 255));
        contentPanel.add(controlPanel, BorderLayout.SOUTH);
        graphPanel = new GraphPanel(dijkstra);
        //graphPanel.setBackground(new Color(150, 240, 150));
        contentPanel.add(graphPanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    private void updateGraphPanel() {
        List<String> NameList = new LinkedList<>();
        for (Vertex vertex : dijkstra.getVertices()) {
            NameList.add(vertex.Name);
        }
        Collections.sort(NameList);
        String[] Names = NameList.toArray(new String[NameList.size()]);
        // Modify the color and size of the startComboBox and endComboBox
        startComboBox.setBackground(Color.WHITE);
        endComboBox.setBackground(Color.WHITE);
        startComboBox.setModel(new DefaultComboBoxModel<>(Names));
        endComboBox.setModel(new DefaultComboBoxModel<>(Names));

        graphPanel.overlayEdges.put("weighted", new LinkedList<>());
        graphPanel.repaint();
    }

    private class DijkstraButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String start = startComboBox.getItemAt(startComboBox.getSelectedIndex());
            String end = endComboBox.getItemAt(endComboBox.getSelectedIndex());

            List<Edge> path = dijkstra.getPath(start, end);
            double totalDistance = dijkstra.totalDistance(start, end);

            if (path.isEmpty()) {
                System.out.println("No path found from " + start + " to " + end);
            } else {
                System.out.println("Shortest path from " + start + " to " + end + ": " + path);
                graphPanel.overlayEdges.put("weighted", path);
                graphPanel.repaint();
                JOptionPane.showMessageDialog(null, "The shortest distance from " + start + " to " + end + " is: " + totalDistance + " km");
            }
            
        }
        
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dijkstra = GraphUtils.readGraph("city_names.csv", "city_pair.csv");
            graphPanel.dijkstra = dijkstra;
            updateGraphPanel();
        }
    }

    public static void main(String[] args) {
        new MainApp();
    }
}
