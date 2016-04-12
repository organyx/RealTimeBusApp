package model.google_items;

import android.content.Context;

import com.google.android.gms.maps.model.Polyline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleks on 10-Apr-16.
 * Route class model for Google Directions API response.
 */
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    private Bound bounds;
    private String copyrights;
    private List<Leg> legs;
    private Polyline overviewPolyLine;
    private String summary;

    public Route(Context context) {
        legs = new ArrayList<>();
    }

    public Route() {
        legs = new ArrayList<>();
    }

    public Bound getBounds() {
        return bounds;
    }

    public void setBounds(Bound bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public void addLeg(Leg leg) {
        this.legs.add(leg);
    }

    public Polyline getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public void setOverviewPolyLine(Polyline overviewPolyLine) {
        this.overviewPolyLine = overviewPolyLine;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "Route{" +
                "bounds=" + bounds +
                ", copyrights='" + copyrights + '\'' +
                ", legs=" + legs +
                ", overviewPolyLine=" + overviewPolyLine +
                ", summary='" + summary + '\'' +
                '}';
    }
}
