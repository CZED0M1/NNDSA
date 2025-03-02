package DataStructures;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;


@Getter
public abstract class Graph<KEdge, VEdge, KVertex, VVertex> implements Serializable  {
    private final HashMap<KVertex, Vertex<KVertex, VVertex>> vertices;
    private final HashMap<KEdge, Edge<KEdge, VEdge>> edges;

    @Data
    public abstract class Vertex<K,V>  implements Serializable {
        private K key;
        private V value;
        private GeoLocation location;
        private final List<Edge<?,?>> edges = new ArrayList<>();

        public Vertex(K key, V value){
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return key.toString() + "(" + value.toString() + ") - " + "(" + edges + ")";
        }

        public void addEdge(Edge<?,?> edge) {
            if (!edges.contains(edge)) {
                edges.add(edge);
            }
        }
    }
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


    public Graph(){
        vertices = new HashMap<>();
        edges = new HashMap<>();
    }

    public void addVertex(Vertex<KVertex, VVertex> vertex){
        if(vertices.containsKey(vertex.getKey())){
            throw new IllegalArgumentException("Vertex with key " + vertex.getKey() + " already exists.");
        }
        vertices.put(vertex.getKey(), vertex);
    }

    public void addEdge(KVertex startKey, KVertex endKey, Edge<KEdge, VEdge> edge){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
        //noinspection SuspiciousMethodCalls
        if(edges.containsKey(key)){
            throw new IllegalArgumentException("Edge with start key " +edge.getKey()+ " already exists.");
        }
        if(!vertices.containsKey(startKey)){
            throw new IllegalArgumentException("Vertex with key " + startKey + " does not exist.");
        }
        if(!vertices.containsKey(endKey)){
            throw new IllegalArgumentException("Vertex with key " + endKey + " does not exist.");
        }

        edges.put(edge.getKey(), edge);
        getVertex(startKey).addEdge(edge);
        getVertex(endKey).addEdge(edge);
    }

    public void removeVertex(KVertex key){
        if(!vertices.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        vertices.remove(key);
    }

    public void removeEdge(KVertex startKey, KVertex endKey){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
        //noinspection SuspiciousMethodCalls
        if(!edges.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        //noinspection SuspiciousMethodCalls
        edges.remove(key);
    }

    public Vertex<KVertex, VVertex> getVertex(KVertex key){
        if(!vertices.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        return vertices.get(key);
    }

    public Edge<KEdge, VEdge> getEdge(KVertex startKey, KVertex endKey){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
        Map.Entry<KVertex,KVertex> OppositeKey = new AbstractMap.SimpleEntry<>(endKey, startKey);
        //noinspection SuspiciousMethodCalls
        if(!edges.containsKey(key) && !edges.containsKey(OppositeKey)){
            throw new IllegalArgumentException("Edge with start vertex key " + key + " does not exist.");
        }
        //noinspection SuspiciousMethodCalls
        return (edges.containsKey(key)) ? edges.get(key) : edges.get(OppositeKey);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices:\n");
        for(Vertex<KVertex, VVertex> vertex : vertices.values()){
            sb.append(vertex.toString()).append("\n");
        }
        sb.append("Edges:\n");
        for(Edge<KEdge, VEdge> edge : edges.values()){
            sb.append(edge.toString()).append("\n");
        }
        return sb.toString();
    }

    public void clear() {
        vertices.clear();
        edges.clear();
    }

}