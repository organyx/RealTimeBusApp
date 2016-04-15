package async_tasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import model.google_items.Leg;
import model.google_items.Route;
import model.google_items.Step;
import utils.JSONParser;
import utils.TaskParameters;


/**
 * Created by Aleks on 29-Mar-16.
 * Async Task for retrieving directions.
 */
public class GetDirectionsTask extends AsyncTask<TaskParameters, String, String> {
    private static final String TAG = "GetDirectionsTask";
    GoogleMap taskMap;
    JSONObject resultSet;

    @Override
    protected String doInBackground(TaskParameters... params) {
        JSONObject result = new JSONObject();
        URL url;
        HttpsURLConnection urlConnection;
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json");
        for (TaskParameters p : params)
        {
            try {
                taskMap = p.getGmap();

                buildUrl(stringBuilder, p);

                url = new URL(stringBuilder.toString());

                urlConnection = (HttpsURLConnection)url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                StringBuilder sb= new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();

                if(HttpResult == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "json: " + sb.toString());
                    // Parse the String to a JSON Object
                    result = new JSONObject(sb.toString());
                }
                else
                {
                    Log.d(TAG, "urlConnection.getResponseMessage(): " + urlConnection.getResponseMessage());
                    result = null;
                }
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                Log.d(TAG, "UnsuppoertedEncodingException: " + e.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.d(TAG, "Error JSONException: " + e.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "IOException: " + e.toString());
            }
        }
        resultSet = result;
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    private void buildUrl(StringBuilder stringBuilder, TaskParameters p) {
        // origin
        final LatLng origin = p.getFrom();
        stringBuilder.append("?origin=")
                .append(origin.latitude)
                .append(',')
                .append(origin.longitude);

        // destination
        final LatLng destination = p.getTo();
        stringBuilder.append("&destination=")
                .append(destination.latitude)
                .append(',')
                .append(destination.longitude);

        // travel
        stringBuilder.append("&mode=").append(p.getTravelMode().getValue());

        // waypoints
        if (p.getWaypoints().size() > 0) {
            stringBuilder.append("&waypoints=");
            if(p.isOptimize())
                stringBuilder.append("optimize:true|");
            for (int i = 0; i < p.getWaypoints().size(); i++) {
                final LatLng points = p.getWaypoints().get(i);
                stringBuilder.append("via:"); // we don't want to parseRoutes the resulting JSON for 'legs'.
                stringBuilder.append(points.latitude);
                stringBuilder.append(",");
                stringBuilder.append(points.longitude);
                stringBuilder.append("|");
            }
        }

        // sensor
        stringBuilder.append("&sensor=true");

        // language
        if (p.getLanguage() != null) {
            stringBuilder.append("&language=").append(p.getLanguage());
        }

        // API key
        if(p.getKey() != null) {
            stringBuilder.append("&key=").append(p.getKey());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String routes) {
        List<Route> routesJson = null;
        JSONParser parser = new JSONParser();
//        routesJson = parser.parseRoutes(resultSet);
        try {
            routesJson = parser.parseRoutes(routes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<LatLng> points;
        PolylineOptions polyLineOptions = null;

        // traversing through routes
        if (routesJson != null) {
            for (int i = 0; i < routesJson.size(); i++) {
                points = new ArrayList<>();
                polyLineOptions = new PolylineOptions();
                Route path = routesJson.get(i);

                Log.d(TAG, path.toString());

                for (Leg leg: path.getLegs()) {
                    for (Step step: leg.getSteps()) {
                        for (LatLng position: step.getPoints()) {
                            points.add(position);
                        }
                    }
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }
        }

        taskMap.addPolyline(polyLineOptions);
    }
}
