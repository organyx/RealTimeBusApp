package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.vacho.realtimebusapp.R;

import java.io.Serializable;

/**
 * Created by Aleks on 05-Apr-16.
 * Model class for Location information.
 */
public class LocationItem implements Comparable<LocationItem>, Serializable, Parcelable {
    private static final long serialVersionUID = 1L;
    private static final String TAG = "LocationItem";
    private boolean showNotify;
    private int id;
    private int icon;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private float zoom;
    private boolean isFavourited;
    private int visits;
    private long date;

    /**
     * Constructor for complete initialization.
     *
     * @param id           ID of the Location in the Database.
     * @param name         Name of the Location.
     * @param address      Address of the Location.
     * @param lat          Latitude of the Location.
     * @param lng          Longitude of the Location.
     * @param zoom         Zoom value for using with Google Maps
     * @param isFavourited Check if the Location is favorite.
     */
    public LocationItem(int id, String name, String address, double lat, double lng, float zoom, boolean isFavourited) {
        this.isFavourited = isFavourited;
        this.id = id;
        this.icon = R.drawable.ic_flag_24dp;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
        this.visits = 0;
        this.date = System.currentTimeMillis();
    }

    /**
     * Constructor for partial initialization.
     *
     * @param name         Name of the Location.
     * @param address      Address of the Location.
     * @param lat          Latitude of the Location.
     * @param lng          Longitude of the Location.
     * @param zoom         Zoom value for using with Google Maps
     * @param isFavourited Check if the Location is favorite.
     */
    public LocationItem(String name, String address, double lat, double lng, float zoom, boolean isFavourited) {
        this.icon = R.drawable.ic_flag_24dp;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.zoom = zoom;
        this.isFavourited = isFavourited;
        this.visits = 0;
        this.date = System.currentTimeMillis();
    }

    /**
     * Constructor for partial initialization.
     *
     * @param name    Name of the Location.
     * @param address Address of the Location.
     */
    public LocationItem(String name, String address) {
        this.icon = R.drawable.ic_flag_24dp;
        this.name = name;
        this.address = address;
        this.visits = 0;
        this.date = System.currentTimeMillis();
    }

    /**
     * Constructor for Bus station initialization.
     *
     * @param name Name of the Location.
     * @param lat  Latitude of the Location.
     * @param lng  Longitude of the Location.
     */
    public LocationItem(String name, double lat, double lng) {
        this.icon = R.drawable.ic_flag_24dp;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.visits = 0;
        this.date = System.currentTimeMillis();
    }

    /**
     * Constructor for Bus station initialization.
     *
     * @param name Name of the Location.
     * @param address Address of the Location.
     * @param lat  Latitude of the Location.
     * @param lng  Longitude of the Location.
     */
    public LocationItem(String name, String address, double lat, double lng) {
        this.icon = R.drawable.ic_flag_24dp;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.visits = 0;
        this.date = System.currentTimeMillis();
    }

    /**
     * Default empty constructor.
     */
    public LocationItem() {
    }

    public LocationItem(Parcel source) {
        Log.d(TAG, "LocationItem(Parcel source) unpacking source.");
        this.id = source.readInt();
        this.icon = source.readInt();
        this.name = source.readString();
        this.address = source.readString();
        this.lat = source.readDouble();
        this.lng = source.readDouble();
        this.zoom = source.readFloat();
        this.isFavourited = source.readInt() == 0;
        this.visits = source.readInt();
        this.date = source.readLong();
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public boolean isFavourited() {
        return isFavourited;
    }

    public void setIsFavourited(boolean isFavourited) {
        this.isFavourited = isFavourited;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public LocationItem setID(int id) {
        LocationItem i = new LocationItem();
        i.setId(id);
        return i;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    @Override
    public int compareTo(@NonNull LocationItem another) {
        return (visits - another.visits);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel" + flags);
        dest.writeInt(id);
        dest.writeInt(icon);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeFloat(zoom);
        dest.writeInt(isFavourited ? 0 : 1);
        dest.writeInt(visits);
        dest.writeLong(date);
    }

    public static final Parcelable.Creator<LocationItem> CREATOR = new Parcelable.Creator<LocationItem>() {
        public LocationItem createFromParcel(Parcel source) {
            return new LocationItem(source);
        }

        public LocationItem[] newArray(int size) {
            return new LocationItem[size];
        }
    };

}
