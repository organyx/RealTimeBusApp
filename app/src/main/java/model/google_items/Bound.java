package model.google_items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Aleks on 10-Apr-16.
 * Bound class model for Google Directions API response.
 */
public class Bound {
    private LatLng northEast;
    private LatLng southWest;

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public void setSouthWest(LatLng southWest) {
        this.southWest = southWest;
    }

    @Override
    public String toString() {
        return "Bound{" +
                "northEast=" + northEast +
                ", southWest=" + southWest +
                '}';
    }
}
