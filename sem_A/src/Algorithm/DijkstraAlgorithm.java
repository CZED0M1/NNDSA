package Algorithm;

import DataStructures.Graph;
import DataStructures.Edge;
import java.util.*;

public class DijkstraAlgorithm<KEdge, VEdge, KVertex, VVertex> {
    public DijkstraResult<KVertex> computeShortestPaths(Graph<KEdge, VEdge, KVertex, VVertex> graph, KVertex source) {
        Map<KVertex, Double> distances = new HashMap<>(); //Nejkratší vzdálenost k vrcholům od zdroje
        Map<KVertex, KVertex> previous = new HashMap<>(); //Výsledná cesta
        //možná jiný seznam??

        // Inicializace: všem vrcholům nastavíme nekonečno a zdroji 0
        for (KVertex vertex : graph.getVertices().keySet()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);

        // PriorityQueue vybírá vrchol s nejmenší známou vzdáleností
        PriorityQueue<KVertex> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        queue.add(source);

        while (!queue.isEmpty()) {
            KVertex current = queue.poll();

            // Projdeme všechny hrany a vybereme ty, které obsahují aktuální vrchol
            for (Edge<KEdge, VEdge> edge : graph.getEdges().values()) {
                @SuppressWarnings("unchecked")
                // Obsahuje oba krajní vrcholy
                Map.Entry<KVertex, KVertex> edgeVertexes = (Map.Entry<KVertex, KVertex>) edge.getKey();
                // Map.entry - K,V
                if (edge.isOpen()) {
                    if (edgeVertexes.getKey().equals(current) || edgeVertexes.getValue().equals(current)) {
                        // Zjištění souseda
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

                            distances.put(neighbor, alt);
                            previous.put(neighbor, current);
                            // Neaktualizovalo by to hodnotu ve frontě, takže musíme ručně odebrat a znovu přidat
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
