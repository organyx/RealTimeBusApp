package model;

/**
 * Created by Vacho on 3/11/2016.
 */
public class BusStationInfo {

    private int icon;
    private String busStationName;
    private String busLines;

    public BusStationInfo(int icon, String busStationName, String busLines) {
        this.icon = icon;
        this.busStationName = busStationName;
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
