package async_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.google.android.gms.maps.GoogleMap;

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
 * Async Task for retrieving directions.
 */
public class GetDirectionsTask extends AsyncTask<TaskParameters, String, String> {
    private static final String TAG = "GetDirectionsTask";
    GoogleMap taskMap;

    @Override
    protected String doInBackground(TaskParameters... params) {
        JSONObject result = new JSONObject();
        URL url;
        HttpsURLConnection urlConnection;
        for (TaskParameters p : params)
        {
            try {
                taskMap = p.getGmap();
                url = new URL("TO DO" + "&key=" + BuildConfig.SERVER_KEY);
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
        return result.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
