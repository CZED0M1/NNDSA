package Grid;

import DataStructures.Location;
import lombok.Data;
import java.util.*;


@Data
public class GridIndex<K> {
    private List<List<Map.Entry<K, Location>>> grid;
    private List<Double> vertical;
    private List<Double> horizontal;
    private boolean splitVertically = true;
    private int blockingFactor;
    private GridFile gridFile;

    public GridIndex(double minX, double minY,double maxX, double maxY) {
        this.grid = new ArrayList<>();
        this.vertical = new ArrayList<>();
        this.horizontal = new ArrayList<>();
        this.gridFile = new GridFile("gridFile.bin");
        this.blockingFactor = gridFile.getBlockingFactor();

        vertical.addFirst(minX);
        vertical.addLast(maxX);
        horizontal.addFirst(minY);
        horizontal.addLast(maxY);
    }

    public void add(K v, Double latitude, Double longitude){
        findSpaceInGrid(v, new Location(latitude, longitude));
    }

    private void findSpaceInGrid(K key, Location location){
        int row = -1;
        int col = -1;

        //Hledání pozice - vertical
        for (int i = 0; i < vertical.size() - 1; i++) {
            if (location.getX() >= vertical.get(i) && location.getX() <= vertical.get(i + 1)) {
                row = i;
                break;
            }
        }

        //Hledání pozice - horizontal
        for (int j = 0; j < horizontal.size() - 1; j++) {
            if (location.getY() >= horizontal.get(j) && location.getY() <= horizontal.get(j + 1)) {
                col = j;
                break;
            }
        }

        while (grid.size() <= row) {
            grid.add(new ArrayList<>());
        }
        while (grid.get(row).size() <= col) {
            grid.get(row).add(null);
        }

        //přečti bytes

        //porovnej počet s blok faktorem


        if (grid.get(row).get(col) != null) {

            Map.Entry<K, Location> existing = grid.get(row).get(col);

            if (existing.getValue().getX() == location.getX() && existing.getValue().getY() == location.getY()) {
                System.out.println("Duplicitní bod");
                return;
            }

            grid.get(row).set(col, null);

            if (!splitVertically && existing.getValue().getY() != location.getY() ||
                    existing.getValue().getX() == location.getX()) {
                double midLong = (existing.getValue().getY() + location.getY()) / 2;
                if (!horizontal.contains(midLong)) {
                    horizontal.add(col + 1, midLong);
                }
                //vertikální split
            }else if (splitVertically && existing.getValue().getX() != location.getX()
                    || existing.getValue().getY() == location.getY()) {
                double midLat = (existing.getValue().getX() + location.getX()) / 2;
                if (!vertical.contains(midLat)) {
                    vertical.add(row + 1, midLat);
                }
            }

            splitVertically = !splitVertically;

            findSpaceInGrid(existing.getKey(),existing.getValue());
            findSpaceInGrid(key,location);
        } else {
            grid.get(row).set(col, Map.entry(key,location));
        }
    }

    public List<K> findRange(Location start, Location end) {
        List<K> outputList = new ArrayList<>();

        int rowStart = -1;
        int colStart = -1;
        int rowEnd = -1;
        int colEnd = -1;

        for (int i = 0; i < vertical.size() - 1; i++) {
            if (start.getX() >= vertical.get(i) && start.getX() < vertical.get(i + 1)) {
                rowStart = i;
            }
            if (end.getX() >= vertical.get(i) && end.getX() < vertical.get(i + 1)) {
                rowEnd = i;
            }
        }

        for (int j = 0; j < horizontal.size() - 1; j++) {
            if (start.getY() >= horizontal.get(j) && start.getY() < horizontal.get(j + 1)) {
                colStart = j;
            }
            if (end.getY() >= horizontal.get(j) && end.getY() < horizontal.get(j + 1)) {
                colEnd = j;
            }
        }

        if (rowStart == -1 || colStart == -1 || rowEnd == -1 || colEnd == -1) {
            return outputList;
        }

        for (int i = rowStart; i <= rowEnd; i++) {
            for (int j = colStart; j <= colEnd; j++) {
                if (i < grid.size() && j < grid.get(i).size() && grid.get(i).get(j) != null) {
                    outputList.add(grid.get(i).get(j).getKey());
                }
            }
        }

        return outputList;
    }

    public K findPoint(Location location) {
        List<K> result = findRange(location, location);
        return result.isEmpty() ? null : result.getFirst();
    }
}
