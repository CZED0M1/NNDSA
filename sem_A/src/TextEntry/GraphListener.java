package TextEntry;

import Algorithm.DijkstraAlgorithm;
import Algorithm.DijkstraResult;
import DataStructures.Graph;
import Implementation.TransportGraph;

import java.util.*;

public class GraphListener {
    private TransportGraph graph;
    DijkstraAlgorithm<Map.Entry<String, String>, Integer, String, Object> dijkstraAlgorithm =new DijkstraAlgorithm<>();
    DijkstraResult<String> dijkstraResult;
    public static String sourceVertex="";



    public GraphListener() {
        graph = new TransportGraph();
    }

    public void startListening() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) {
                break;
            }
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];

        try {
            switch (action) {
                case "help":
                    System.out.println("Commands:");
                    System.out.println("addVertex <vertexName> <population>");
                    System.out.println("addEdge <startVertex> <endVertex> <distance>");
                    System.out.println("removeVertex <vertexName>");
                    System.out.println("removeEdge <startVertex> <endVertex>");
                    System.out.println("getVertex <vertexName>");
                    System.out.println("getEdge <startVertex> <endVertex>");
                    System.out.println("clear");
                    System.out.println("print");
                    System.out.println("load <file>");
                    System.out.println("save <file>");
                    System.out.println("setStart <startVertex>");
                    System.out.println("getShortestPath <vertexName>");
                    System.out.println("printPath <startVertex> <endVertex>");
                    System.out.println("closeEdge <startVertex> <endVertex>");
                    System.out.println("openEdge <startVertex> <endVertex>");
                    System.out.println("isEdgeOpen <startVertex> <endVertex>");
                    System.out.println("getTable");
                    break;
                case "addVertex":
                    graph.addVertex(graph.new CityVertex(parts[1], Integer.parseInt(parts[2])));
                    System.out.println("Vertex added.");
                    break;
                case "addEdge":
                    String startVertex = parts[1];
                    String endVertex = parts[2];
                    int distance = Integer.parseInt(parts[3]);
                    graph.addEdge(startVertex, endVertex, graph.new RoadEdge(startVertex, endVertex, distance));
                    System.out.println("Edge added.");
                    break;
                case "removeVertex":
                    graph.removeVertex(parts[1]);
                    System.out.println("Vertex removed.");
                    break;
                case "removeEdge":
                    graph.removeEdge(parts[1], parts[2]);
                    System.out.println("Edge removed.");
                    break;
                case "getVertex":
                    Graph.Vertex vertex = graph.getVertex(parts[1]);
                    System.out.println("Vertex: " + vertex);
                    break;
                case "getEdge":
                    Graph.Edge edge = graph.getEdge(parts[1], parts[2]);
                    System.out.println("Edge: " + edge);
                    break;
                case "clear":
                    graph.clear();
                    System.out.println("Graph cleared.");
                    break;
                case "print":
                    System.out.println(graph);
                    break;
                case "load":
                    graph = Serialization.LoadFile.loadGraph("graph.ser");
                    System.out.println("Graph loaded.");
                    break;
                case "save":
                    Serialization.SaveFile.saveGraph(graph, parts[1]);
                    System.out.println("Graph saved.");
                    break;
                case "setStart":
                    //noinspection rawtypes
                    dijkstraResult = dijkstraAlgorithm.computeShortestPaths((Graph) graph, parts[1]);
                    sourceVertex=parts[1];
                    break;
                case "getShortestPath":
                    System.out.println(dijkstraResult.getDistances().get(parts[1]));
                    break;
                case "printPath":
                    List<String> path = dijkstraAlgorithm.getShortestPath(dijkstraResult.getPrevious(), parts[1], parts[2]);
                    System.out.println(path);
                    break;
                case "closeEdge":
                    graph.getEdge(parts[1], parts[2]).close();
                    System.out.println("Edge closed.");
                    break;
                case "openEdge":
                    graph.getEdge(parts[1], parts[2]).open();
                    System.out.println("Edge opened.");
                    break;
                case "isEdgeOpen":
                    System.out.println(graph.getEdge(parts[1], parts[2]).isOpen());
                    break;
                case "getTable":
                        graph.getVertices().forEach((key, _) -> System.out.println(key +
                                " " + dijkstraAlgorithm.getShortestPath(dijkstraResult.getPrevious(), sourceVertex, key)));
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        } catch (Exception e) {
            System.out.println("Error processing command: " + e.getMessage());
        }
    }
}