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

    public Dijkstra() {
        vertexNames = new HashMap<String, Vertex>(); // Hashmap to store the vertices

    }    
    public Collection<Vertex> getVertices() {
        return vertexNames.values();
    }

    // Get vertex object with the given name
    public Vertex getVertex(String cityName) {
        return vertexNames.get(cityName);
    }
    // Add a vertex to the dijkstra
    public void addVertex (Vertex vertex) {
        System.out.println("vertexname: " + vertex.cityName);
        if (vertexNames.containsKey(vertex.cityName)) {
            throw new IllegalArgumentException("Vertex already exists.");
        }
        vertexNames.put(vertex.cityName, vertex);
    }

    // Add a directed edge from one vertex to another vertex
    public void addEdge(String city1, String city2, double weight) {
        if (!vertexNames.containsKey(city1)) {
            System.out.println("city 1: " + city1);
            throw new IllegalArgumentException(city1 + " does not exist.");
        }
        if (!vertexNames.containsKey(city2)) {
            System.out.println("city 1: " + city2);
            throw new IllegalArgumentException(city2 + " does not exist.");
        }
    
        Vertex start = vertexNames.get(city1);
        Vertex end = vertexNames.get(city2);
        Edge newEdge = new Edge(start, end, weight);
        start.addEdge(newEdge);
    }

    // Add an undirected edge from one vertex to another vertex
    public void addUndirectedEdge(String city1, String city2, double cost) {
        addEdge(city1, city2, cost);
        addEdge(city2, city1, cost);
    }

    public void dijkstra(String name) {
        Vertex start = vertexNames.get(name);
        start.distance = 0;
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
    public List<Edge> getPath(String city1, String city2) {
        dijkstra(city1);

        List<Edge> pathEdges = new ArrayList<>();
        Vertex end = vertexNames.get(city2);

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
    public double totalDistance(String city1, String city2) {
        List<Edge> path = getPath(city1, city2);
        double totalDistance = 0.0;
        for (Edge e : path) {
            totalDistance += e.distance;
        }
        return totalDistance;
    }

    public void printPath(String city1, String city2) {
        List<Edge> path = getPath(city1, city2);
    
        if (path.isEmpty()) {
            System.out.println("No path found from " + city1 + " to " + city2);
        } else {
            System.out.print("The shortest path from " + city1 + " to " + city2 + " is: ");
            StringBuilder pathDescription = new StringBuilder();
    
            for (int i = 0; i < path.size(); i++) {
                Edge e = path.get(i);
                String fromCity = e.start.cityName;
                String toCity = e.end.cityName;
                double distance = e.distance;
    
                pathDescription.append("(")
                               .append(fromCity)
                               .append(" - ")
                               .append(toCity)
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
            InputStream inputStream = Dijkstra.class.getResourceAsStream("/city_names.csv");
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

            InputStream inputStream1 = Dijkstra.class.getResourceAsStream("/city_pair.csv");
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

        String city1 = "Hiep Binh Chanh post office";
        String city2 = "Tan Hung post office";
            
        dijkstra.printPath(city1, city2);
        
        

    }

}