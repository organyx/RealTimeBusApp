package utils;

import android.util.Log;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aleks on 24-Mar-16.
 * Class for managing PubNub API
 */
public class PubNubManager {
    public final static String TAG = "PUBNUB";

    /**
     * Method for initializing PubNub.
     * @return new Instance initialized with PubNub keys.
     */
    public static Pubnub startPubnub() {
        Log.d(TAG, "Initializing PubNub");
        return new Pubnub(BuildConfig.PUBLISH_KEY, BuildConfig.SUBSCRIBE_KEY);
    }

    /**
     * Method for subscribing to specific channel.
     * @param mPubnub PubNub instance.
     * @param channelName PubNub channel name.
     * @param subscribeCallback PubNub callback.
     */
    public static void subscribe(Pubnub mPubnub, String channelName, Callback subscribeCallback) {
        // Subscribe to channel
        try {
            mPubnub.subscribe(channelName, subscribeCallback);
            Log.d(TAG, "Subscribed to Channel");
        } catch (PubnubException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Method for posting data to specific channel.
     * @param pubnub PubNub instance.
     * @param channelName PubNub channel name.
     * @param latitude Device latitude.
     * @param longitude Device longitude.
     * @param altitude Device altitude.
     */
    public static void broadcastLocation(Pubnub pubnub, String channelName, double latitude,
                                         double longitude, double altitude) {
        JSONObject message = new JSONObject();
        try {
            message.put("lat", latitude);
            message.put("lng", longitude);
            message.put("alt", altitude);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        Log.d(TAG, "Sending JSON Message: " + message.toString());
        pubnub.publish(channelName, message, publishCallback);
    }

    /**
     * Callback for handling responses.
     */
    public static Callback publishCallback = new Callback() {

        @Override
        public void successCallback(String channel, Object response) {
            Log.d("PUBNUB", "Sent Message: " + response.toString());
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.d("PUBNUB", error.toString());
        }
    };
}
