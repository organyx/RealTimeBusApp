package utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.google_route_items.Distance;
import model.google_route_items.Duration;
import model.google_route_items.Leg;
import model.google_route_items.Route;
import model.google_route_items.Step;

/**
 * Created by Aleks on 29-Mar-16.
 * Utility class for parsing Google Directions API response.
 */
public class JSONParser {

    private static final String ROUTES = "routes";
    private static final String SUMMARY = "summary";
    private static final String LEGS = "legs";
    private static final String DISTANCE = "distance";
    private static final String TEXT = "text";
    private static final String VALUE = "value";
    private static final String DURATION = "duration";
    private static final String STEPS = "steps";
    private static final String END_LOCATION = "end_location";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String HTML_INSTRUCTION = "html_instructions";
    private static final String POLYLINE = "polyline";
    private static final String POINTS = "points";
    private static final String START_LOCATION = "start_location";

    public List<Route> parse(String routesJSONString) throws Exception {
        List<Route> routeList = new ArrayList<>();
        final JSONObject jSONObject = new JSONObject(routesJSONString);
        JSONArray routeJSONArray = jSONObject.getJSONArray(ROUTES);
        Route route;
        JSONObject routesJSONObject;
        for (int m = 0; m < routeJSONArray.length(); m++) {
            route = new Route();
            routesJSONObject = routeJSONArray.getJSONObject(m);
            JSONArray legsJSONArray;
            route.setSummary(routesJSONObject.getString(SUMMARY));
            legsJSONArray = routesJSONObject.getJSONArray(LEGS);
            JSONObject legJSONObject;
            Leg leg;
            JSONArray stepsJSONArray;
            for (int b = 0; b < legsJSONArray.length(); b++) {
                leg = new Leg();
                legJSONObject = legsJSONArray.getJSONObject(b);
                leg.setDistance(new Distance(legJSONObject.optJSONObject(DISTANCE).optString(TEXT), legJSONObject.optJSONObject(DISTANCE).optLong(VALUE)));
                leg.setDuration(new Duration(legJSONObject.optJSONObject(DURATION).optString(TEXT), legJSONObject.optJSONObject(DURATION).optLong(VALUE)));
                stepsJSONArray = legJSONObject.getJSONArray(STEPS);
                JSONObject stepJSONObject, stepDurationJSONObject, legPolyLineJSONObject, stepStartLocationJSONObject, stepEndLocationJSONObject;
                Step step;
                String encodedString;
                LatLng stepStartLocationLatLng, stepEndLocationLatLng;
                for (int i = 0; i < stepsJSONArray.length(); i++) {
                    stepJSONObject = stepsJSONArray.getJSONObject(i);
                    step = new Step();
                    JSONObject stepDistanceJSONObject = stepJSONObject.getJSONObject(DISTANCE);
                    step.setDistance(new Distance(stepDistanceJSONObject.getString(TEXT), stepDistanceJSONObject.getLong(VALUE)));
                    stepDurationJSONObject = stepJSONObject.getJSONObject(DURATION);
                    step.setDuration(new Duration(stepDurationJSONObject.getString(TEXT), stepDurationJSONObject.getLong(VALUE)));
                    stepEndLocationJSONObject = stepJSONObject.getJSONObject(END_LOCATION);
                    stepEndLocationLatLng = new LatLng(stepEndLocationJSONObject.getDouble(LATITUDE), stepEndLocationJSONObject.getDouble(LONGITUDE));
                    step.setEndLocation(stepEndLocationLatLng);
                    step.setHtmlInstructions(stepJSONObject.getString(HTML_INSTRUCTION));
                    legPolyLineJSONObject = stepJSONObject.getJSONObject(POLYLINE);
                    encodedString = legPolyLineJSONObject.getString(POINTS);
                    step.setPoints(decodePoly(encodedString));
                    stepStartLocationJSONObject = stepJSONObject.getJSONObject(START_LOCATION);
                    stepStartLocationLatLng = new LatLng(stepStartLocationJSONObject.getDouble(LATITUDE), stepStartLocationJSONObject.getDouble(LONGITUDE));
                    step.setStartLocation(stepStartLocationLatLng);
                    leg.addStep(step);
                }
                route.addLeg(leg);
            }
            routeList.add(route);
        }
        return routeList;
    }
    /**
     * Method Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
