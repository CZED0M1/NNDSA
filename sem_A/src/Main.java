import Algorithm.DijkstraAlgorithm;
import Algorithm.DijkstraResult;
import DataStructures.Graph;
import Implementation.CityVertex;
import Implementation.RoadEdge;
import Implementation.TransportGraph;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        TransportGraph graph = new TransportGraph();

        // Vytvoření uzlů
        CityVertex cityA = new CityVertex("Praha", 1300000);
        CityVertex cityB = new CityVertex("Brno", 380000);
        CityVertex cityC = new CityVertex("Ostrava", 290000);
        CityVertex cityD = new CityVertex("Plzeň", 170000);
        CityVertex cityE = new CityVertex("Liberec", 100000);
        CityVertex testV = new CityVertex("Test",1);

        // Vytvoření hran
        RoadEdge roadAB = new RoadEdge("Praha", "Brno", 200);
        RoadEdge roadAC = new RoadEdge("Praha", "Ostrava", 350);
        RoadEdge roadAD = new RoadEdge("Praha", "Plzeň", 90);
        RoadEdge roadAE = new RoadEdge("Praha", "Liberec", 110);
        RoadEdge roadBC = new RoadEdge("Brno", "Ostrava", 170);
        RoadEdge roadBD = new RoadEdge("Brno", "Plzeň", 250);
        RoadEdge roadCD = new RoadEdge("Ostrava", "Plzeň", 400);
        RoadEdge roadCE = new RoadEdge("Ostrava", "Liberec", 320);
        RoadEdge roadDE = new RoadEdge("Plzeň", "Liberec", 200);
        RoadEdge test = new RoadEdge("Brno","Test",1);
        RoadEdge test2 = new RoadEdge("Test","Liberec",1);
        test2.close();

        // Přidání uzlů do grafu
        graph.addVertex(cityA);
        graph.addVertex(cityB);
        graph.addVertex(cityC);
        graph.addVertex(cityD);
        graph.addVertex(cityE);
        graph.addVertex(testV);

        // Přidání hran do grafu
        graph.addEdge("Praha", "Brno", roadAB);
        graph.addEdge("Praha", "Ostrava", roadAC);
        graph.addEdge("Praha", "Plzeň", roadAD);
        graph.addEdge("Praha", "Liberec", roadAE);
        graph.addEdge("Brno", "Ostrava", roadBC);
        graph.addEdge("Brno", "Plzeň", roadBD);
        graph.addEdge("Ostrava", "Plzeň", roadCD);
        graph.addEdge("Ostrava", "Liberec", roadCE);
        graph.addEdge("Plzeň", "Liberec", roadDE);
        graph.addEdge("Brno","Test",test);
        graph.addEdge("Test","Liberec",test2);

        DijkstraAlgorithm<Map.Entry<String, String>, Integer, String, Object> dijkstra = new DijkstraAlgorithm<>();
        DijkstraResult<String> result = dijkstra.computeShortestPaths((Graph) graph, "Brno");

        Map<String, Double> distances = result.getDistances();
        System.out.println(distances.get("Liberec"));

        List<String> path = dijkstra.getShortestPath(result.getPrevious(), "Brno", "Liberec");
        System.out.println(path);



    }
}