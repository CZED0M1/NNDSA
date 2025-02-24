package DataStructures;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Vertex<K,V> implements Serializable {
    private K key;
    private V value;
    private GeoLocation location;
    private final List<K> edges = new ArrayList<>();

    public Vertex(K key, V value){
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return value.toString() + " - " + edges;
    }

    public void addEdge(K key) {
        if (!edges.contains(key)) {
            edges.add(key);
        }
    }
}
