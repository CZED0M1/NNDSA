package Grid;

import DataStructures.GeoLocation;
import DataStructures.Graph;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Data
public class GridIndex<KVertex> {
    private List<List<KVertex>> grid;
    private List<Double> vertical;
    private List<Double> horizontal;
    private Graph graph;


    public GridIndex(Graph graph) {
        this.graph = graph;
        this.vertical = new ArrayList<>();
        this.horizontal = new ArrayList<>();
        this.grid = new ArrayList<>();
        createGrid();
    }

    private void createGrid() {
        List<KVertex> vertices = graph.getVerticesKeys();
        PriorityQueue<Double> sortedLatitude = new PriorityQueue<>();
        PriorityQueue<Double> sortedLongitude = new PriorityQueue<>();
        for (KVertex vertex : vertices) {
            double latitude = graph.getLocation(vertex).getLatitude();
            double longitude = graph.getLocation(vertex).getLongitude();
            sortedLatitude.add(latitude);
            sortedLongitude.add(longitude);
        }

        vertical.add(0.0);
        horizontal.add(0.0);

        for (int i = 0; i < vertices.size(); i++) {
            if (i % 2 == 0) {
                //Vertical
                decomposeGrid(sortedLatitude,sortedLongitude, vertical);
            } else {
                //Horizontal
                decomposeGrid(sortedLongitude,sortedLatitude, horizontal);
            }
        }
        fillGrid();
    }

    private void fillGrid() {
        ALL_VERTICES:
        for (Object key : graph.getVerticesKeys()) {
            for (int i = 0; i < vertical.size(); i++) {
                grid.add(new ArrayList<>());
                if (graph.getLocation(key).getLatitude() < vertical.get(i)) {
                    for (int j = 0; j < horizontal.size(); j++) {
                        if (graph.getLocation(key).getLongitude() < horizontal.get(j)) {
                            while (grid.get(i - 1).size() < j - 1) {
                                grid.get(i - 1).add(null);
                            }
                            grid.get(i - 1).add(j - 1, (KVertex) key);
                            continue ALL_VERTICES;
                        }
                    }
                }
            }
        }
    }

    private void decomposeGrid(PriorityQueue<Double> sortedList,PriorityQueue<Double> secondSortedList, List<Double> splitList) {
        if (sortedList.isEmpty()) { return;}
        if (sortedList.size() == 1) {
            splitList.add(sortedList.poll());
            return;
        }
        @SuppressWarnings("ConstantConditions")
        double a = sortedList.poll();
        secondSortedList.poll();
        @SuppressWarnings("ConstantConditions")
        double b = sortedList.poll();
        secondSortedList.poll();
        //TODO zjistit možnost více vertexů v jedné bunce
            double result = ((a + b) / 2);
            splitList.add(result);


    }

    public List<KVertex> findRange(GeoLocation start, GeoLocation end) {
        List<KVertex> outputList = new ArrayList<>();
        ALL_VERTICES:
        for (Object key : graph.getVerticesKeys()) {
            for (int i = 0; i < vertical.size(); i++) {
                if (graph.getLocation(key).getLatitude() >= start.getLatitude() &&
                    graph.getLocation(key).getLatitude() <= end.getLatitude()) {
                    for (int j = 0; j < horizontal.size(); j++) {
                        if (graph.getLocation(key).getLongitude() >= start.getLongitude() &&
                            graph.getLocation(key).getLongitude() <= end.getLongitude()) {
                                outputList.add((KVertex) key);
                                continue ALL_VERTICES;
                        }
                    }
                }
            }
        }
        return outputList;
    }

    public KVertex findPoint(GeoLocation location) {
        if (findRange(location,location).size()>1) {
            System.out.println(findRange(location,location).size());

        }
      return findRange(location,location).getFirst();
    }
}
