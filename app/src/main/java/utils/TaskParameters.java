package utils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aleks on 29-Mar-16.
 * Class to Handle sending parameters to Async tasks.
 */
public class TaskParameters {
    GoogleMap gmap;
    LatLng location;
    LatLng from;
    LatLng to;

    /**
     * Constructor for requesting information about a specific location.
     * @param map GoogleMap that displays the information.
     * @param location Location for research.
     */
    public TaskParameters(GoogleMap map, LatLng location)
    {
        this.gmap = map;
        this.location = location;
    }

    /**
     * Constructor for requesting information about a specific route.
     * @param map GoogleMap that displays the information.
     * @param from Starting location for the route.
     * @param to Destination location for the route.
     */
    public TaskParameters(GoogleMap map, LatLng from, LatLng to)
    {
        this.gmap = map;
        this.from = from;
        this.to = to;
    }

    public LatLng getTo() {
        return to;
    }

    public void setTo(LatLng to) {
        this.to = to;
    }

    public LatLng getFrom() {
        return from;
    }

    public void setFrom(LatLng from) {
        this.from = from;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public GoogleMap getGmap() {
        return gmap;
    }

    public void setGmap(GoogleMap gmap) {
        this.gmap = gmap;
    }
}
