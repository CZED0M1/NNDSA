package Serialization;

import Implementation.CityVertex;
import Implementation.RoadEdge;
import Implementation.TransportGraph;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LoadFile {
    public static TransportGraph loadGraph(String fileName) throws IOException, ClassNotFoundException {
        TransportGraph graph = new TransportGraph();

        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            int vertexCount = in.readInt();
            for (int i = 0; i < vertexCount; i++) {
                CityVertex vertex = (CityVertex) in.readObject();
                graph.addVertex(vertex);
            }

            int edgeCount = in.readInt();
            for (int i = 0; i < edgeCount; i++) {
                RoadEdge edge = (RoadEdge) in.readObject();
                graph.addEdge(edge.getKey().getKey(), edge.getKey().getValue(), edge);
            }
        }
        return graph;
    }
}
