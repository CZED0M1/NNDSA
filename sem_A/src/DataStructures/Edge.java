package DataStructures;


import java.io.Serializable;

public abstract class Edge<K, V> implements Serializable {
    private K key;
    private V value;
    private boolean isOpen = true;

    public Edge(K key, V value){
        this.value = value;
        this.key = key;
    }

    public K getKey(){
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue(){
        return value;
    }

    public void setValue(V value){
        this.value = value;
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
        return value.toString();
    }
}
