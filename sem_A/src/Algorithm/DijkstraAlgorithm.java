package Algorithm;

import DataStructures.Graph;
import DataStructures.Graph.Edge;

import java.util.*;

public class DijkstraAlgorithm<KEdge, VEdge, KVertex, VVertex> {
    public DijkstraResult<KVertex> computeShortestPaths(Graph<KEdge, VEdge, KVertex, VVertex> graph, KVertex source) {
        Map<KVertex, Double> distances = new HashMap<>(); //Nejkratší vzdálenost k vrcholům od zdroje
        Map<KVertex, KVertex> previous = new HashMap<>(); //Výsledná cesta

        // Inicializace: všem vrcholům nastavíme nekonečno a zdroji 0
        for (KVertex vertex : graph.getVertices().keySet()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);

        // PriorityQueue vybírá vrchol s nejmenší známou vzdáleností -> source
        PriorityQueue<KVertex> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        queue.add(source);

        while (!queue.isEmpty()) {
            KVertex current = queue.poll(); //remove first element from queue

            Graph.Vertex vertex = graph.getVertex(current);
            //TODO předělat abych neměl vertex a edge

            // Projdeme všechny hrany a vybereme ty, které obsahují aktuální vrchol
            List<Edge> edges = vertex.getEdges();
            for (Edge edge: edges) {

                // Obsahuje oba krajní vrcholy
                Map.Entry<KVertex, KVertex> edgeVertexes = (Map.Entry<KVertex, KVertex>) edge.getKey();
                // Map.entry - K,V

                if (edge.isOpen()) {
                    if (edgeVertexes.getKey().equals(current) || edgeVertexes.getValue().equals(current)) {
                        // Zjištění 2.prvku hrany
                        KVertex neighbor;
                        if (edgeVertexes.getKey().equals(current)) {
                            neighbor = edgeVertexes.getValue();
                        } else {
                            neighbor = edgeVertexes.getKey();
                        }

                        // délka trasy
                        Integer weight = (Integer) edge.getValue();
                        // délka k sousedovi
                        double alt = distances.get(current) + weight;

                        if (alt < distances.get(neighbor)) {
                            distances.put(neighbor, alt); //add or replace
                            previous.put(neighbor, current); //add or replace
                            // Pokud už obsahuje, tak odebereme a přidáme znovu
                            queue.remove(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return new DijkstraResult<>(distances, previous);
    }

    public List<KVertex> getShortestPath(Map<KVertex, KVertex> previous, KVertex source, KVertex target) {
        LinkedList<KVertex> path = new LinkedList<>();
        KVertex current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(source)) {
                break;
            }
            //nastav předchůdce
            current = previous.get(current);
        }
        if (path.isEmpty() || !path.getFirst().equals(source)) {
            // Cesta neexistuje
            return Collections.emptyList();
        }
        return path;
    }
}
