package DataStructures;


import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Vertex<K,V> implements Serializable {
    private K key;
    private V value;
    private GeoLocation location;

    public Vertex(K key, V value){
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }


}
