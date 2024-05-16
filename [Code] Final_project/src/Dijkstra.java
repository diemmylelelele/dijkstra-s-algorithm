import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Dijkstra {

    private Map<String, Vertex> vertexNames;
    // Constructor to initialize the Dijkstra
    public Dijkstra() {
        vertexNames = new HashMap<String, Vertex>(); // Hashmap to store the vertices
    }

    public Collection<Vertex> getVertices() {
        return vertexNames.values();
    }

    // Get vertex object with the given name
    public Vertex getVertex(String Name) {
        return vertexNames.get(Name);
    }

    // Add a vertex to the dijkstra
    public void addVertex (Vertex vertex) {
        //System.out.println("vertexname: " + vertex.Name);
        if (vertexNames.containsKey(vertex.Name)) {
            throw new IllegalArgumentException("Vertex already exists.");
        }
        vertexNames.put(vertex.Name, vertex);
    }

    // Add a directed edge from one vertex to another vertex
    public void addEdge(String location1, String location2, double weight) {
        if (!vertexNames.containsKey(location1)) {
            System.out.println("Location 1: " + location1);
            throw new IllegalArgumentException(location1 + " does not exist.");
        }
        if (!vertexNames.containsKey(location2)) {
            System.out.println("Location 2: " + location2);
            throw new IllegalArgumentException(location2 + " does not exist.");
        }

        Vertex start = vertexNames.get(location1); // Get the start vertex
        Vertex end = vertexNames.get(location2);   // Get the end vertex
        Edge newEdge = new Edge(start, end, weight); // Create a new edge
        start.addEdge(newEdge);   // Add the edge to the start vertex
    }

    // Add an undirected edge from one vertex to another vertex
    public void addUndirectedEdge(String location1, String location2, double cost) {
        addEdge(location1, location2, cost);
        addEdge(location2, location1, cost);
    }

    public void dijkstra(String name) {
        Vertex start = vertexNames.get(name);
        start.distance = 0;  // Set the distance of the start vertex to 0
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(new Comparator<Vertex>() { // Priority queue to store the vertices
            public int compare(Vertex v1, Vertex v2) { // Compare the distance of two vertices
                return Double.compare(v1.distance, v2.distance);
            }
        });
        vertexQueue.add(start);
        
        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();
            u.settled = true;

            for (Edge e : u.adjacentEdges) {
                Vertex v = e.end;
                if (v.settled) {
                    continue;
                }
                double weight = e.distance;
                double distanceThroughU = u.distance + weight;
                if (distanceThroughU < v.distance) {
                    vertexQueue.remove(v);
                    v.distance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
        
    }
    public List<Edge> getPath(String location1, String location2) {
        dijkstra(location1);

        List<Edge> pathEdges = new ArrayList<>();
        Vertex end = vertexNames.get(location2);

        // Reconstruct the path from end to start using the `previous` links
        while (end != null && end.previous != null) {
            Vertex start = end.previous;
            for (Edge e : start.adjacentEdges) {
                if ((e.start == start && e.end == end) || (e.start == end && e.end == start)) {
                    pathEdges.add(e);
                    break;
                }
            }
            end = start; // Move to the previous vertex in the path
        }

        Collections.reverse(pathEdges); // Reverse to get the path from city1 to city2
        return pathEdges;
    }
    public double totalDistance(String location1, String location2) {
        List<Edge> path = getPath(location1, location2);
        double totalDistance = 0.0;
        for (Edge e : path) {
            totalDistance += e.distance;
        }
        return totalDistance;
    }

    public void printPath(String location1, String location2) {
        List<Edge> path = getPath(location1, location2);
    
        if (path.isEmpty()) {
            System.out.println("No path found from " + location1 + " to " + location2);
        } else {
            System.out.print("The shortest path from " + location1 + " to " + location2 + " is: ");
            StringBuilder pathDescription = new StringBuilder();
    
            for (int i = 0; i < path.size(); i++) {
                Edge e = path.get(i);
                String from = e.start.Name;
                String to = e.end.Name;
                double distance = e.distance;
    
                pathDescription.append("(")
                               .append(from)
                               .append(" - ")
                               .append(to)
                               .append(", ")
                               .append(distance)
                               .append(")");    
                if (i < path.size() - 1) {
                    pathDescription.append(", ");
                }
            }
            System.out.println(pathDescription.toString());
        }
    }

    public static void main(String[] args) {

        Dijkstra dijkstra = new Dijkstra();

        try {
            InputStream inputStream = Dijkstra.class.getResourceAsStream("/names.csv");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(streamReader);
            String line = br.readLine();
            while (line != null) {
                String[] vertex = line.split(",");
                if (vertex.length >= 3) {
                    String cityName = vertex[0];
                    int x = Integer.parseInt(vertex[1]);
                    int y = Integer.parseInt(vertex[2]);
                    Vertex v = new Vertex(cityName, x, y);
                    dijkstra.addVertex(v);
                    line = br.readLine();
                    // Process the data
                }

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
        String city1 = "BC Hiep Binh Chanh";
        String city2 = "BC Tan Hung";
            
        dijkstra.printPath(city1, city2);
        
        

    }

}