import java.util.LinkedList;
import java.util.List;

public class Vertex {
    public String cityName;
    public int x;
    public int y;
    public boolean settled;
    public double distance;
    public Vertex previous;
    public List<Edge> adjacentEdges;

    public Vertex(String cityName, int x, int y) {
        this.cityName = cityName;
        this.x = x;
        this.y = y;
        settled = false;
        distance = Double.MAX_VALUE;
        previous = null;
        adjacentEdges = new LinkedList<Edge>();
    }
    public void addEdge (Edge edge) {
        adjacentEdges.add(edge);

    }
}

