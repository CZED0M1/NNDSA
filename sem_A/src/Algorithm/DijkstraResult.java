package Algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DijkstraResult<KVertex> {
    private Map<KVertex, Double> distances;
    private Map<KVertex, KVertex> previous;

}

