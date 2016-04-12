package model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Aleks on 08-Apr-16.
 * Model class for Bus Line information.
 */
public class BusLineItem {
    private String busLineName;
    private List<LocationItem> busStations;
    private int[] busStationOrder;

    /**
     * Default empty constructor
     */
    public BusLineItem() {
    }

    /**
     * Constructor for initialization without order.
     * @param busLineName Bus Line name.
     * @param busStations Bus Station list.
     */
    public BusLineItem(String busLineName, List<LocationItem> busStations) {
        this.busLineName = busLineName;
        this.busStations = busStations;
    }

    /**
     * Constructor for complete initialization.
     * @param busLineName Bus Line name.
     * @param busStations Bus Station list.
     * @param busStationOrder Bus Station order.
     */
    public BusLineItem(String busLineName, List<LocationItem> busStations, int[] busStationOrder) {
        this.busLineName = busLineName;
        this.busStations = busStations;
        this.busStationOrder = busStationOrder;
    }

    public int[] getBusStationOrder() {
        return busStationOrder;
    }

    public void setBusStationOrder(int[] busStationOrder) {
        this.busStationOrder = busStationOrder;
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
                ", busStationOrder=" + Arrays.toString(busStationOrder) +
                '}';
    }
}
