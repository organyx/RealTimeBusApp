package model;

import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Vacho on 3/11/2016.
 */
public class BusStationInfo {

    private int icon;
    private String busStationName;
    private String busLines;
    private LatLng location;

    public BusStationInfo(String busStationName, String busLines, LatLng location) {
        this.icon = R.drawable.ic_flag_24dp;
        this.busStationName = busStationName;
        this.busLines = busLines;
        this.location = location;
    }

    public BusStationInfo(String busStationName, String busLines) {
        this.icon = R.drawable.ic_flag_24dp;
        this.busStationName = busStationName;
        this.busLines = busLines;
    }

    public BusStationInfo(String busLines){
        this.icon = R.drawable.ic_flag_24dp;
        this.busLines = busLines;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBusStationName() {
        return busStationName;
    }

    public void setBusStationName(String busStationName) {
        this.busStationName = busStationName;
    }

    public String getBusLines() {
        return busLines;
    }

    public void setBusLines(String busLines) {
        this.busLines = busLines;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BusStationInfo{" +
                "busStationName='" + busStationName + '\'' +
                ", busLines='" + busLines + '\'' +
                ", location=" + location +
                '}';
    }
}
