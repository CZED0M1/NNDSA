package DataStructures;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Edge<K, V> implements Serializable {
    private K key;
    private V value;
    private boolean isOpen = true;

    public Edge(K key, V value){
        this.value = value;
        this.key = key;
    }


    public boolean isOpen(){
        return isOpen;
    }

    public void close(){
        isOpen = false;
    }

    public void open(){
        isOpen = true;
    }

    public String toString() {
        return this.key + " - " + value.toString();
    }
}
