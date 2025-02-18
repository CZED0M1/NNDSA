package Implementation;

import DataStructures.Edge;
import DataStructures.Vertex;

import java.util.AbstractMap;
import java.util.Map;


public class RoadEdge extends Edge<Map.Entry<String,String>, Integer> {
    public RoadEdge(Vertex<String, Integer> startVertex, //TODO: zeptat se
                    Vertex<String, Integer> endVertex,
                    Integer distance) {
        super(new AbstractMap.SimpleEntry<>(startVertex.getKey(), endVertex.getKey()), distance);
    }
}
