package model;

import java.util.List;

/**
 * Created by Aleks on 08-Apr-16.
 * Model class for Bus Line information.
 */
public class BusLineItem {
    private String busLineName;
    private List<LocationItem> busStations;

    /**
     * Default empty constructor
     */
    public BusLineItem() {
    }

    /**
     * Constructor for complete initialization.
     * @param busLineName Bus Line name.
     * @param busStations Bus Station list.
     */
    public BusLineItem(String busLineName, List<LocationItem> busStations) {
        this.busLineName = busLineName;
        this.busStations = busStations;
    }

    public String getBusLineName() {
        return busLineName;
    }

    public void setBusLineName(String busLineName) {
        this.busLineName = busLineName;
    }

    public List<LocationItem> getBusStations() {
        return busStations;
    }

    public void setBusStations(List<LocationItem> busStations) {
        this.busStations = busStations;
    }

    public void addBusStation(LocationItem busStation) {
        this.busStations.add(busStation);
    }

    public LocationItem getBusStation(int i) {
        return this.busStations.get(i);
    }

    @Override
    public String toString() {
        return "BusLineItem{" +
                "busLineName='" + busLineName + '\'' +
                ", busStations=" + busStations +
                '}';
    }
}
