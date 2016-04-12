package utils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Aleks on 29-Mar-16.
 * Class to Handle sending parameters to Async tasks.
 */
public class TaskParameters {
    private GoogleMap gmap;
    private LatLng location;
    private LatLng from;
    private LatLng to;

    private TravelMode travelMode;
    private boolean alternativeRoutes;
    private List<LatLng> waypoints;
    private boolean optimize;
    private String language;
    private String key;

    private int radius;
    private PlaceType placeType;

    public enum PlaceType {
        BUS_STATION("bus_station"),
        GAS_STATION("gas_station"),
        TRAIN_STATION("train_station"),
        TRANSIT_STATION("transit_station"),
        SUBWAY_STATION("subway_station");

        protected String _sValue;

        PlaceType(String sValue) {
            this._sValue = sValue;
        }

        public String getValue() {
            return _sValue;
        }
    }

    public enum TravelMode {
        BIKING("bicycling"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String _sValue;

        TravelMode(String sValue) {
            this._sValue = sValue;
        }

        public String getValue() {
            return _sValue;
        }
    }

    /**
     * Constructor for requesting information about a specific location.
     *
     * @param map      GoogleMap that displays the information.
     * @param location Location for research.
     */
    public TaskParameters(GoogleMap map, LatLng location) {
        this.gmap = map;
        this.location = location;
    }

    /**
     * Constructor for requesting information about a specific route.
     *
     * @param map  GoogleMap that displays the information.
     * @param from Starting location for the route.
     * @param to   Destination location for the route.
     */
    public TaskParameters(GoogleMap map, LatLng from, LatLng to) {
        this.gmap = map;
        this.from = from;
        this.to = to;
    }

    /**
     * Constructor for requesting information about a specific route with waipoints.
     *
     * @param gmap      GoogleMap that displays the information.
     * @param from      Starting location for the route.
     * @param to        Destination location for the route.
     * @param wayPoints Waypoints for the route.
     */
    public TaskParameters(GoogleMap gmap, LatLng from, LatLng to, List<LatLng> wayPoints) {
        this.gmap = gmap;
        this.from = from;
        this.to = to;
        this.waypoints = wayPoints;
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

    public List<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public boolean isAlternativeRoutes() {
        return alternativeRoutes;
    }

    public void setAlternativeRoutes(boolean alternativeRoutes) {
        this.alternativeRoutes = alternativeRoutes;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }
}
