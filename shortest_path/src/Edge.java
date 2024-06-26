public class Edge {
    public Vertex start;
    public Vertex end;
    public double distance;

    public Edge(Vertex start, Vertex end, double distance) {
        this.start = start;
        this.end = end;
        this.distance = distance;
    }
}