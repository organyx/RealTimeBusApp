package async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import utils.TaskParameters;

/**
 * Created by Aleks on 29-Mar-16.
 * Async Task for retrieving nearby bus stations.
 */
public class GetNearestBusStations extends AsyncTask<TaskParameters, String, String> {
    private static final String TAG = "GetNearestBusStations";
    GoogleMap taskMap;

    @Override
    protected String doInBackground(TaskParameters... params) {
        JSONObject result = new JSONObject();
        URL url;
        HttpsURLConnection urlConnection;
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        for (TaskParameters p : params) {
            try {
                taskMap = p.getGmap();

                buildUrl(stringBuilder, p);

                url = new URL(stringBuilder.toString());

                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                StringBuilder sb = new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();

                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, "json: " + sb.toString());
                    // Parse the String to a JSON Object
                    result = new JSONObject(sb.toString());
                } else {
                    Log.d(TAG, "urlConnection.getResponseMessage(): " + urlConnection.getResponseMessage());
                    result = null;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d(TAG, "UnsuppoertedEncodingException: " + e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Error JSONException: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "IOException: " + e.toString());
            }
        }

        return result.toString();
    }

    private void buildUrl(StringBuilder stringBuilder, TaskParameters p) {
        // Location
        final LatLng location = p.getLocation();
        stringBuilder.append("?location=")
                .append(location.latitude)
                .append(",")
                .append(location.longitude);

        // Radius
        if (p.getRadius() != 0)
            stringBuilder.append("&radius=").append(p.getRadius());
        else
            stringBuilder.append("&radius=500");

        // Type
        if (p.getPlaceType() != null)
            stringBuilder.append("&type=").append(p.getPlaceType().getValue());
        else
            stringBuilder.append("&type=bus_station");

        // language
        if (p.getLanguage() != null)
            stringBuilder.append("&language=").append(p.getLanguage());
        else
            stringBuilder.append("&language=en");

        // API key
        if(p.getKey() != null)
            stringBuilder.append("&key=").append(p.getKey());
        else
            stringBuilder.append("&key=").append(BuildConfig.SERVER_KEY);
    }

    @Override
    protected void onPreExecute() {
        // we can start a progress bar here
    }

    @Override
    protected void onPostExecute(String result) {
//            Toast.makeText(getActivity(), "Loaded\n" + result, Toast.LENGTH_SHORT).show();
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray objArray = obj.getJSONArray("results");
            for (int i = 0; i < objArray.length(); i++) {
                JSONObject explrObject = objArray.getJSONObject(i);
                Log.d(TAG, explrObject.getString("name"));
//                    Log.d(TAG, String.valueOf(explrObject.getJSONArray("geometry").getJSONArray(0).getDouble(0)));
//                    Log.d(TAG, explrObject.getJSONObject("geometry").toString());
//                    Log.d(TAG, explrObject.getJSONObject("geometry").getJSONObject("location").toString());
                Log.d(TAG, String.valueOf(explrObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat")));
                taskMap.addMarker(new MarkerOptions()
                        .title(explrObject.getString("name"))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_36dp))
                        .position(new LatLng(
                                explrObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                explrObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng"))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
