import java.util.*;
public class Vertex {
    public String Name;
    public int x;
    public int y;
    public boolean settled;
    public double distance;
    public Vertex previous;
    public List<Edge> adjacentEdges;

    public Vertex(String Name, int x, int y) {
        this.Name = Name;
        this.x = x; // x-coordinate
        this.y = y; // y-coordinate
        settled = false;
        distance = Double.MAX_VALUE;
        previous = null;
        adjacentEdges = new LinkedList<Edge>();
    }

    public void addEdge (Edge edge) {
        adjacentEdges.add(edge);
    }
}

