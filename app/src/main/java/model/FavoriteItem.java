package model;

/**
 * Created by Aleks on 07-Mar-16.
 */
public class FavoriteItem {

    private boolean showNotify;
    private long id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String zoom;

    @Override
    public String toString() {
        return "FavoriteItem{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", zoom='" + zoom + '\'' +
                '}';
    }

    public FavoriteItem()
    {

    }

    public FavoriteItem(boolean showNotify, long id, String name, String address, double lat, double lng, String zoom) {
        this.showNotify  = showNotify;
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
    }

    public FavoriteItem(String name, String address, double lat, double lng, String zoom) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public boolean isShowNotify()
    {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify)
    {
        this.showNotify = showNotify;
    }
}
