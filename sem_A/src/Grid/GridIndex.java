package Grid;

import DataStructures.GeoLocation;
import DataStructures.Graph;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Data
public class GridIndex<K> {
    private List<List<K>> verticesKeys;
    private List<Double> vertical;
    private List<Double> horizontal;
    private Graph graph;

    public GridIndex(Graph graph) {
        this.graph = graph;
        this.vertical = new ArrayList<>();
        this.horizontal = new ArrayList<>();
        this.verticesKeys = new ArrayList<>();
        createGrid();
    }

    private void createGrid() {
        //Naplnění seznamů hodnotami z grafu
        List<K> vertices = graph.getVerticesKeys();
        PriorityQueue<Double> sortedLatitude = new PriorityQueue<>();
        PriorityQueue<Double> sortedLongitude = new PriorityQueue<>();
        for (K vertex : vertices) {
            double latitude = graph.getLocation(vertex).getLatitude();
            double longitude = graph.getLocation(vertex).getLongitude();
            sortedLatitude.add(latitude);
            sortedLongitude.add(longitude);
        }

        int size = vertices.size();

        for (int i = 0; i < size; i++) {
            if(i % 2 == 0) {
                //Vertical
                decomposeGrid(sortedLatitude, vertical, sortedLatitude.poll());
            } else {
                //Horizontal
                decomposeGrid(sortedLongitude, horizontal, sortedLatitude.poll());
            }
        }
        fillGrid(size);
    }

    private void fillGrid(int size) {
        //TODO
        for (int i = 0; i < size; i++) {
            List<K> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                //TODO podle rozdělení vertical + horizontal
            }
            verticesKeys.add(row);
        }
    }

    private void decomposeGrid(PriorityQueue<Double> sortedLatitude, List<Double> vertical, Double poll) {
        if(sortedLatitude.size() == 1) {
            vertical.add(poll);
            return;
        }
        @SuppressWarnings("ConstantConditions")
        double a = sortedLatitude.poll();
        @SuppressWarnings("ConstantConditions")
        double b = sortedLatitude.poll();
        double result = ((a+b)/2);
        vertical.add(result);
    }

    private int findRange(GeoLocation start, GeoLocation end) {
        return 0;
    }
    private int findPoint(GeoLocation location) {
        return 0;
    }

}
