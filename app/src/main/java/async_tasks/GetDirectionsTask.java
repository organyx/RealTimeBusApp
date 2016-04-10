package async_tasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vacho.realtimebusapp.BuildConfig;
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

import model.google_route_items.Leg;
import model.google_route_items.Route;
import model.google_route_items.Step;
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
        for (TaskParameters p : params)
        {
            try {
                taskMap = p.getGmap();
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + p.getFrom().latitude + "," + p.getFrom().longitude
                        + "&destination=" + p.getTo().latitude + "," + p.getTo().longitude
                        + "&waypoints=55.8622125,9.8420348|55.8630615,9.8481180|55.8645606,9.8721935|55.8696416,9.8752405|55.8718567,9.8820211"
                        + "&region=dk"
                        + "&key=" + BuildConfig.SERVER_KEY);
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String routes) {
        List<Route> routesJson = null;
        JSONParser parser = new JSONParser();
//        routesJson = parser.parse(resultSet);
        try {
            routesJson = parser.parse(routes);
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

                for (int n = 0; n < path.getLegs().size(); n++){
                    Leg leg = path.getLegs().get(n);
                    for (int m = 0; m < leg.getSteps().size(); m++)
                    {
                        Step step = leg.getSteps().get(m);
                        for (int k = 0; k < step.getPoints().size(); k++){
                            LatLng position = step.getPoints().get(k);
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
