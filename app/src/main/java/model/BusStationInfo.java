package model;

import com.example.vacho.realtimebusapp.R;

/**
 * Created by Vacho on 3/11/2016.
 */
public class BusStationInfo {

    private int icon;
    private String busStationName;
    private String busLines;

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


}
