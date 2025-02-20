package DataStructures;

import lombok.Data;

@Data
public class GeoLocation {
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
