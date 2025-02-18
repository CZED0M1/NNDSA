import Implementation.CityVertex;
import Implementation.RoadEdge;
import Implementation.TransportGraph;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TransportGraph transportGraph = new TransportGraph();

        transportGraph = Serialization.LoadFile.loadGraph("graph.ser");
        System.out.println(transportGraph);

    }
}