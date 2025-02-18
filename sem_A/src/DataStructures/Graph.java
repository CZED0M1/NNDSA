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

    public void addEdge(Edge<KEdge,VEdge> edge){
        if(edges.containsKey(edge.getKey())){
            throw new IllegalArgumentException("Edge with start key " +edge.getKey()+ " already exists.");
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

    public void removeEdge(KEdge key){
        if(!edges.containsKey(key)){
            throw new IllegalArgumentException("Edge with vertex key " + key + " does not exist.");
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

    public Edge<KEdge, VEdge> getEdge(KEdge key){
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