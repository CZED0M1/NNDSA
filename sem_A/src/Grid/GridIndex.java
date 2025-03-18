package Grid;

import DataStructures.GeoLocation;
import DataStructures.Graph;
import lombok.Data;

import java.util.*;

@Data
public class GridIndex<KVertex> {
    private List<List<KVertex>> grid;
    private List<Double> vertical;
    private List<Double> horizontal;
    private HashMap<KVertex, GeoLocation> vertices;

    private boolean splitVertically = true;
    private final double OFFSET = 5.0;

    public GridIndex() {
        this.vertical = new ArrayList<>();
        this.horizontal = new ArrayList<>();
        this.grid = new ArrayList<>();
        this.vertices = new HashMap<>();
    }

    public void add(KVertex v,Double latitude, Double longitude) {
        vertices.put(v, new GeoLocation(latitude, longitude));
    }

    public void createGrid() {
        vertical.add(Collections.min(vertices.values(), Comparator.comparingDouble(GeoLocation::getLatitude)).getLatitude() - OFFSET);
        vertical.add(Collections.max(vertices.values(), Comparator.comparingDouble(GeoLocation::getLatitude)).getLatitude() + OFFSET);
        horizontal.add(Collections.min(vertices.values(), Comparator.comparingDouble(GeoLocation::getLongitude)).getLongitude() - OFFSET);
        horizontal.add(Collections.max(vertices.values(), Comparator.comparingDouble(GeoLocation::getLongitude)).getLongitude() + OFFSET);

        vertices.forEach(this::findSpaceInGrid);
    }

    private void findSpaceInGrid(KVertex key, GeoLocation location) {
        int row = -1;
        int col = -1;

        for (int i = 0; i < vertical.size() - 1; i++) {
            if (location.getLatitude() >= vertical.get(i) && location.getLatitude() <= vertical.get(i + 1)) {
                row = i;
                break;
            }
        }

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
            System.out.println("Kolize v buňce na pozici [" + row + ", " + col + "]. Rozděluji...");

            KVertex existing = grid.get(row).get(col);
            grid.get(row).set(col, null); // Odstraníme původní prvek

            if (vertices.get(existing).getLatitude()== vertices.get(key).getLatitude() && vertices.get(existing).getLongitude()== vertices.get(key).getLongitude()) {
                System.out.println("stejný prvek?!");
                return ;
            }
            else if (!splitVertically && vertices.get(existing).getLongitude() != vertices.get(key).getLongitude() ||
                    vertices.get(existing).getLatitude() == vertices.get(key).getLatitude()) {
                // Rozdělení vodorovně
                double midLong = (vertices.get(existing).getLongitude() + vertices.get(key).getLongitude()) / 2;
                if (!horizontal.contains(midLong)) {
                    horizontal.add(col + 1, midLong); // Přidáme na konkrétní index
                }
            }else if (splitVertically && vertices.get(existing).getLatitude() != vertices.get(key).getLatitude()
            || vertices.get(existing).getLongitude() == vertices.get(key).getLongitude()) {
                // Rozdělení svisle
                double midLat = (vertices.get(existing).getLatitude() + vertices.get(key).getLatitude()) / 2;
                if (!vertical.contains(midLat)) {
                    vertical.add(row + 1, midLat); // Přidáme na konkrétní index
                }
            }

            // Přepni způsob dělení pro příští rozdělení
            splitVertically = !splitVertically;

            // Znovu vložíme oba vrcholy do podmřížky
            findSpaceInGrid(existing,vertices.get(existing));
            findSpaceInGrid(key,vertices.get(key));
        } else {
            // Pokud je buňka prázdná, vložíme prvek
            grid.get(row).set(col, key);
            System.out.println("Vloženo do gridu na pozici [" + row + ", " + col + "]: " + key);
        }
    }



    public List<KVertex> findRange(GeoLocation start, GeoLocation end) {
        List<KVertex> outputList = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                KVertex key = grid.get(i).get(j);
                if (key != null) {
                    GeoLocation location = vertices.get(key);
                    if (location.getLatitude() >= start.getLatitude() &&
                            location.getLatitude() <= end.getLatitude() &&
                            location.getLongitude() >= start.getLongitude() &&
                            location.getLongitude() <= end.getLongitude()) {
                        outputList.add(key);
                    }
                }
            }
        }
        return outputList;
    }

    public KVertex findPoint(GeoLocation location) {
        List<KVertex> result = findRange(location, location);
        if (result.size() > 1) {
            System.out.println("Více prvků na stejné pozici: " + result.size());
        }
        return result.isEmpty() ? null : result.get(0);
    }
}
