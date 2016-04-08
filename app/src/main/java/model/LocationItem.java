package model;

/**
 * Created by Aleks on 05-Apr-16.
 * Model class for Location information.
 */
public class LocationItem {

    private boolean showNotify;
    private long id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String zoom;
    private boolean isFavourited;

    /**
     * Constructor for complete initialization.
     * @param id ID of the Location in the Database.
     * @param name Name of the Location.
     * @param address Address of the Location.
     * @param lat Latitude of the Location.
     * @param lng Longitude of the Location.
     * @param zoom Zoom value for using with Google Maps
     * @param isFavourited Check if the Location is favorite.
     */
    public LocationItem(long id, String name, String address, double lat, double lng, String zoom, boolean isFavourited) {
        this.isFavourited = isFavourited;
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
    }

    /**
     * Constructor for partial initialization.
     * @param name Name of the Location.
     * @param address Address of the Location.
     * @param lat Latitude of the Location.
     * @param lng Longitude of the Location.
     * @param zoom Zoom value for using with Google Maps
     * @param isFavourited Check if the Location is favorite.
     */
    public LocationItem(String name, String address, double lat, double lng, String zoom, boolean isFavourited) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
        this.isFavourited = isFavourited;
    }

    /**
     * Constructor for Bus station initialization.
     * @param name Name of the Location.
     * @param lat Latitude of the Location.
     * @param lng Longitude of the Location.
     */
    public LocationItem(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Default empty constructor.
     */
    public LocationItem() {
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isFavourited() {
        return isFavourited;
    }

    public void setIsFavourited(boolean isFavourited) {
        this.isFavourited = isFavourited;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", zoom='" + zoom + '\'' +
                ", isFavourited=" + isFavourited +
                '}';
    }
}
