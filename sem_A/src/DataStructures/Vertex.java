package DataStructures;

public abstract class Vertex<K,V>{
    private K key;
    private V value;
    private GeoLocation location;

    public Vertex(K key, V value){
        this.key = key;
        this.value = value;
    }

    public K getKey(){
        return key;
    }

    public V getValue(){
        return value;
    }

    public void setKey(K key){
        this.key = key;
    }

    public void setValue(V value){
        this.value = value;
    }

    public String toString() {
        return value.toString();
    }


}
