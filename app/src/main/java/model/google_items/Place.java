package model.google_items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aleks on 12-Apr-16.
 * Place class model for Google Directions API response.
 */
public class Place {
    private String id;
    private String name;
    private String vicinity;
    private LatLng location;

    public Place() {
    }

    public Place(String name, String vicinity, LatLng location) {
        this.name = name;
        this.vicinity = vicinity;
        this.location = location;
    }

    public Place(String id, String name, String vicinity, LatLng location) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", location=" + location +
                '}';
    }
}
