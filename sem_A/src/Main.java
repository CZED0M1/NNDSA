import Implementation.CityVertex;
import Implementation.RoadEdge;
import Implementation.TransportGraph;

public class Main {
    public static void main(String[] args) {
        TransportGraph transportGraph = new TransportGraph();

        CityVertex cityVertex = new CityVertex("CityA", 25);
        CityVertex cityVertex2 = new CityVertex("City2", 30);

        RoadEdge roadEdge = new RoadEdge("CityA", "City2", 10);

        transportGraph.addVertex(cityVertex);
        transportGraph.addVertex(cityVertex2);

        transportGraph.addEdge("CityA","City2",roadEdge);

        System.out.println(transportGraph.getEdge("CityA","City2"));

        System.out.println(transportGraph.getVertex("CityA"));

        System.out.println(transportGraph.getVerticesCount());
        System.out.println(transportGraph.getEdgesCount());
        transportGraph.removeEdge("CityA","City2");
        transportGraph.removeVertex("CityA");
        System.out.println(transportGraph.getVerticesCount());
        System.out.println(transportGraph.getEdgesCount());
        System.out.println(transportGraph);
    }
}