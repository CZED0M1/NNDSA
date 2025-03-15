package DataStructures;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeoLocation implements Serializable {
    private double latitude;
    private double longitude;

    public GeoLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String toString(){
        return "(" + latitude + ", " + longitude + ")";
    }
}
