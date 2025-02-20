package Serialization;

import DataStructures.Edge;
import DataStructures.Graph;
import DataStructures.Vertex;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SaveFile {
    public static void saveGraph(Graph<?, ?, ?, ?> graph, String fileName) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            Map<?, ? extends Vertex<?,?>> vertices = graph.getVertices();
            out.writeInt(vertices.size());
            vertices.values().forEach(vertex -> {
                try {
                    out.writeObject(vertex);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Map<?, ? extends Edge<?,?>> edges = graph.getEdges();
            out.writeInt(edges.size());
            edges.values().forEach(edge -> {
                try {
                    out.writeObject(edge);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
