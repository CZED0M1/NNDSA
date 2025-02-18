import Implementation.CityVertex;
import Implementation.RoadEdge;
import Implementation.TransportGraph;

public class Main {
    public static void main(String[] args) {
        TransportGraph transportGraph = new TransportGraph();

        CityVertex cityVertex = new CityVertex("City", 25);
        CityVertex cityVertex2 = new CityVertex("City2", 30);

        RoadEdge roadEdge = new RoadEdge(cityVertex, cityVertex2, 10);

        transportGraph.addVertex(cityVertex);
        transportGraph.addVertex(cityVertex2);
        transportGraph.addEdge(roadEdge);

        System.out.println(transportGraph);
    }
}