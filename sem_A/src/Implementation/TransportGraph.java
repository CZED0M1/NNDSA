package Implementation;

import DataStructures.Graph;

import java.util.AbstractMap;
import java.util.Map;

public class TransportGraph extends Graph<Map.Entry<String, String>, Integer, String, Integer>{

    public TransportGraph() {
        super();
    }

    public class RoadEdge extends Edge<Map.Entry<String, String>, Integer>{
        public RoadEdge(String startKey, String endKey, Integer distance) {
            super(new AbstractMap.SimpleEntry<>(startKey, endKey), distance);
        }
    }

    public class CityVertex extends Vertex<String, Integer>{
        public CityVertex(String name, Integer population) {
            super(name, population);
        }
    }
}
