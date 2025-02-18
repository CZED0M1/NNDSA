package Implementation;

import DataStructures.Edge;
import DataStructures.Vertex;

import java.util.AbstractMap;
import java.util.Map;


public class RoadEdge extends Edge<Map.Entry<String,String>, Integer> {
    public RoadEdge(String startKey, //TODO: zeptat se
                    String endKey,
                    Integer distance) {
        super(new AbstractMap.SimpleEntry<>(startKey, endKey), distance);
    }
}
