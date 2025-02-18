package DataStructures;

import java.util.*;

public abstract class Graph<KEdge, VEdge, KVertex, VVertex> {
    private int verticesCount=0;
    private int edgesCount=0;
    private HashMap<KVertex, Vertex<KVertex, VVertex>> vertices;
    private HashMap<KEdge, Edge<KEdge, VEdge>> edges;

    public Graph(){
        vertices = new HashMap<>();
        edges = new HashMap<>();
    }

    public int getVerticesCount(){
        return verticesCount;
    }

    public int getEdgesCount(){
        return edgesCount;
    }

    public void addVertex(Vertex<KVertex, VVertex> vertex){
        if(vertices.containsKey(vertex.getKey())){
            throw new IllegalArgumentException("Vertex with key " + vertex.getKey() + " already exists.");
        }
        vertices.put(vertex.getKey(), vertex);
        verticesCount++;
    }

    public void addEdge(KVertex startKey, KVertex endKey, Edge<KEdge, VEdge> edge){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
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
        edgesCount++;
    }

    public void removeVertex(KVertex key){
        if(!vertices.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        vertices.remove(key);
        verticesCount--;
    }

    public void removeEdge(KVertex startKey, KVertex endKey){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
        if(!edges.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        edges.remove(key);
        edgesCount--;
    }

    public Vertex<KVertex, VVertex> getVertex(KVertex key){
        if(!vertices.containsKey(key)){
            throw new IllegalArgumentException("Vertex with key " + key + " does not exist.");
        }
        return vertices.get(key);
    }

    public Edge<KEdge, VEdge> getEdge(KVertex startKey, KVertex endKey){
        Map.Entry<KVertex,KVertex> key = new AbstractMap.SimpleEntry<>(startKey, endKey);
        if(!edges.containsKey(key)){
            throw new IllegalArgumentException("Edge with start vertex key " + key + " does not exist.");
        }
        return edges.get(key);
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

}