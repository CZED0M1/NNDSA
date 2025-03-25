package Grid;

import DataStructures.GeoLocation;
import lombok.Data;
import java.util.*;

@Data
public class GridIndex<K> {
    private List<List<Map.Entry<K,GeoLocation>>> grid;
    private List<Double> vertical;
    private List<Double> horizontal;
    private boolean splitVertically = true;

    public GridIndex() {
        this.grid = new ArrayList<>();
        this.vertical = new ArrayList<>();
        this.horizontal = new ArrayList<>();

        //Vložení hranic - min,max
        vertical.addFirst(0.0);
        vertical.addLast(70.0);
        horizontal.addFirst(0.0);
        horizontal.addLast(70.0);
    }

    public void add(K v, Double latitude, Double longitude){
        findSpaceInGrid(v, new GeoLocation(latitude, longitude));
    }

    private void findSpaceInGrid(K key, GeoLocation location){
        int row = -1;
        int col = -1;

        //Hledání pozice - vertical
        for (int i = 0; i < vertical.size() - 1; i++) {
            if (location.getLatitude() >= vertical.get(i) && location.getLatitude() <= vertical.get(i + 1)) {
                row = i;
                break;
            }
        }

        //Hledání pozice - horizontal
        for (int j = 0; j < horizontal.size() - 1; j++) {
            if (location.getLongitude() >= horizontal.get(j) && location.getLongitude() <= horizontal.get(j + 1)) {
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

        if (grid.get(row).get(col) != null) {

            Map.Entry<K,GeoLocation> existing = grid.get(row).get(col);

            if (existing.getValue().getLatitude() == location.getLatitude() && existing.getValue().getLongitude() == location.getLongitude()) {
                System.out.println("Duplicitní bod");
                return;
            }

            grid.get(row).set(col, null);

            if (!splitVertically && existing.getValue().getLongitude() != location.getLongitude() ||
                    existing.getValue().getLatitude() == location.getLatitude()) {
                double midLong = (existing.getValue().getLongitude() + location.getLongitude()) / 2;
                if (!horizontal.contains(midLong)) {
                    horizontal.add(col + 1, midLong);
                }
            //vertikální split
            }else if (splitVertically && existing.getValue().getLatitude() != location.getLatitude()
            || existing.getValue().getLongitude() == location.getLongitude()) {
                double midLat = (existing.getValue().getLatitude() + location.getLatitude()) / 2;
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

        //TODO kontorola pomocí vertical a horizontal
    public List<K> findRange(GeoLocation start,GeoLocation end) {
        List<K> outputList = new ArrayList<>();

        int rowStart = -1;
        int colStart = -1;

        int rowEnd = -1;
        int colEnd = -1;

        for (int i = 0; i < vertical.size() - 1; i++) {
            if (start.getLatitude() <= vertical.get(i)) {
                rowStart = i;
                System.out.println("rowStart: " + rowStart);
                break;
            }
        }

        //Hledání pozice - horizontal
        for (int j = 0; j < horizontal.size() - 1; j++) {
            if (start.getLongitude() <= horizontal.get(j)) {
                colStart = j;
                System.out.println("colStart: " + colStart);
                break;
            }
        }


        for (int i = 0; i < vertical.size() - 1; i++) {
            if (end.getLatitude() <= vertical.get(i)) {
                rowEnd = i;
                System.out.println("rowEnd: " + rowEnd);
                break;
            }
        }

        //Hledání pozice - horizontal
        for (int j = 0; j < horizontal.size() - 1; j++) {
            if (end.getLongitude() <= horizontal.get(j)) {
                colEnd = j;
                System.out.println("colEnd: " + colEnd);
                break;
            }
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

    public K findPoint(GeoLocation location) {
        List<K> result = findRange(location, location);
        return result.isEmpty() ? null : result.getFirst();
    }
}
