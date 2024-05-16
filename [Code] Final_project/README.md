Shortest Path Visualization Project
This project implements Dijkstra's algorithm for finding the shortest path between vertices in a graph. It consists of three main Java classes:

1. Dijkstra.java
This class represents the implementation of Dijkstra's algorithm for computing the shortest paths in a weighted graph. It includes methods for:

Adding vertices and edges to the graph.
Computing the shortest path from a specified start vertex to all other vertices.
Retrieving the shortest path and its total distance between two specified vertices.
2. Edge.java
This class defines an Edge object used to represent connections between vertices in the graph. Each edge has a start vertex, an end vertex, and a distance (weight).

3. MainApp.java
This class provides a graphical user interface (GUI) for visualizing the shortest path computations using Dijkstra's algorithm. It includes:

A GraphPanel class for drawing the graph and displaying the vertices and edges.
Methods to read graph data from CSV files (names.csv and pair.csv) and construct the graph using Dijkstra class.
GUI controls to select start and end vertices, compute the shortest path, and display the result on the graph.

How to Run
To run the project:
- Compile all Java files (Dijkstra.java, Edge.java, MainApp.java) in your Java development environment.
- Execute the MainApp class. This will open the GUI window.
- Select a start and end vertex from the dropdown menus.
- Click "Draw Dijkstra's Path" to compute and display the shortest path between the selected vertices on the graph.
- Use the "Reset" button to reload the graph and reset the visualization.