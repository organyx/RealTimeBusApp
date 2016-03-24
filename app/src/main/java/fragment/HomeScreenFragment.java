package fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.example.vacho.realtimebusapp.HomeScreen;
import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import adapter.CustomListViewAdapter;
import model.BusStationInfo;
import model.FavoriteItem;
import model.HomeListView;
import service.GPSTracker;
import utils.PubNubManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private View transparentView;
    private View whiteSpaceView;
    private View transparentHeaderView;
    private View spaceHeaderView;

    private HomeListView homeListView;
    private SlidingUpPanelLayout slidingPaneLayout;
    private LatLng horsens;
    private GoogleMap googleMap;
    private MapView mapView;
    private static final int REQUEST_LOCATION = 0;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int RESULT_OK = 100;

    private static final String TAG = "HomeScreenFrag";
    public int pos = 0;
    FavoriteItem favoriteItem;
    GPSTracker gpsTracker;

    private MenuItem btnTrack;
    private static final String PUBNUB_TAG = "PUBNUB";
    private boolean isFirstMessage = true;
    private boolean requestingLocationUpdates = false;
    private PolylineOptions mPolylineOptions;
    private Marker mMarker;
    private MarkerOptions mMarkerOptions;
    private LatLng mLatLng;
    private Pubnub pubnub;
    private String channelName = "my_channel";
    private Activity mActivity;
    private Object m;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String fav_item_name = bundle.getString("fav_item_name");
            String fav_item_address = bundle.getString("fav_item_address");
            double fav_item_lat = bundle.getDouble("fav_item_lat");
            double fav_item_lng = bundle.getDouble("fav_item_lng");
            String fav_item_zoom = bundle.getString("fav_item_zoom");
            if (fav_item_name != null &&
                    fav_item_address != null &&
                    fav_item_lat != 0 &&
                    fav_item_lng != 0 &&
                    fav_item_zoom != null) {
                favoriteItem = new FavoriteItem(fav_item_name, fav_item_address, fav_item_lat, fav_item_lng, fav_item_zoom);
                Toast.makeText(getActivity(), "fav_item_name " + favoriteItem.getName(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "fav_item_name " + favoriteItem.getName());
            }
//            getActivity().getActionBar().setTitle(R.string.title_home);
        }

        homeListView = (HomeListView) v.findViewById(android.R.id.list);
        homeListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        slidingPaneLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_drawer);
        slidingPaneLayout.setEnableDragViewTouchEvents(true);

        //Getting the display Height
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);

        slidingPaneLayout.setPanelHeight(mapHeight); // you can use different height here
        slidingPaneLayout.setScrollableView(homeListView, mapHeight);
        slidingPaneLayout.setPanelSlideListener(this);

        transparentView = v.findViewById(R.id.transparentView);
        whiteSpaceView = v.findViewById(R.id.whiteSpaceView);

        transparentHeaderView = inflater.inflate(R.layout.transparent_header_view, homeListView, false);
        spaceHeaderView = transparentHeaderView.findViewById(R.id.space);

        expandMap();

        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidingPaneLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                slidingPaneLayout.onPanelDragged(0);
            }
        });

        mActivity = getActivity();
        setHasOptionsMenu(true); // For Handling Fragment calls to menu items
        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//
//        }
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.home_fragment);
//        mapView = (MapView)view.findViewById(R.id.home_fragment);
//        mapView.getMapAsync(this);
        fragment.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        whiteSpaceView.setVisibility(View.VISIBLE);

        BusStationInfo busStationInfos[] = new BusStationInfo[]{
                new BusStationInfo(R.drawable.ic_action_search, "ads", "123"),
                new BusStationInfo(R.drawable.ic_action_search, "wqeqwekjq", "kjqwejkqhew"),
                new BusStationInfo(R.drawable.ic_action_search, "ads", "123"),
                new BusStationInfo(R.drawable.ic_action_search, "wqeqwekjq", "kjqwejkqhew"),
                new BusStationInfo(R.drawable.ic_action_search, "ads", "123"),
                new BusStationInfo(R.drawable.ic_action_search, "wqeqwekjq", "kjqwejkqhew"),
                new BusStationInfo(R.drawable.ic_action_search, "ads", "123"),
                new BusStationInfo(R.drawable.ic_action_search, "wqeqwekjq", "kjqwejkqhew"),
                new BusStationInfo(R.drawable.ic_action_search, "ads", "123"),
                new BusStationInfo(R.drawable.ic_action_search, "wqeqwekjq", "kjqwejkqhew"),
                new BusStationInfo(R.drawable.ic_action_search, "eeew", "vcvcv")
        };

        homeListView.addHeaderView(transparentHeaderView);
        homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, busStationInfos));
        homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slidingPaneLayout.collapsePane();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        btnTrack = menu.findItem(R.id.action_tracking);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map Ready");
//        this.googleMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
//            else
//            {
//                this.googleMap.setMyLocationEnabled(true);
//                Log.d(TAG, "MyLocation: " + googleMap.isMyLocationEnabled());
//            }
//        }
        googleMap.setMyLocationEnabled(true);
        Log.d(TAG, "MyLocation: " + googleMap.isMyLocationEnabled());

//        this.googleMap.setTrafficEnabled(true);
//        this.googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        googleMap.setPadding(20, 20, 20, 20);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        Log.d(TAG, "isCompassEnabled: " + googleMap.getUiSettings().isCompassEnabled());
        Log.d(TAG, "isMyLocationButtonEnabled: " + googleMap.getUiSettings().isMyLocationButtonEnabled());

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "MarkerCLicked");
//                new GooglePlaceTask().execute();
//                int PLACE_PICKER_REQUEST = 1;
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                try {
//                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
                return false;
            }
        });
        this.googleMap = googleMap;
        if(!requestingLocationUpdates)
        {
            if(favoriteItem == null)
            {
                setDefaultLocation(googleMap);
            }
            else
            {
                setFavLocation(googleMap);
            }
        }

//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addConnectionCallbacks(getActivity())
//                .addOnConnectionFailedListener(getActivity())
//                .addApi(LocationServices.API)
//                .build();
//        googleMap.setMyLocationEnabled(true);
//
//        googleMap.setM
//
//        gpsTracker = new GPSTracker(getActivity());
//        if(gpsTracker.canGetLocation()) {
//            LatLng loc = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//            googleMap.setLocationSource((LocationSource) gpsTracker.getLocation());
//        }
//        else
//        {
//            gpsTracker.showSettingsAlert();
//        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, getActivity());
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_tracking:
                Log.d(TAG, "'Tracking' Button Pressed");
                requestingLocationUpdates = !requestingLocationUpdates;
                if (requestingLocationUpdates) {
                    startFollowingLocation();
                    btnTrack.setTitle("Tracking");
                }
                if (!requestingLocationUpdates) {
                    stopFollowingLocation();
                    btnTrack.setTitle("Track");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startFollowingLocation() {
        initializePolyline();
        pubnub = PubNubManager.startPubnub();
        PubNubManager.subscribe(pubnub, channelName, subscribeCallback);
    }

    private void stopFollowingLocation() {
        pubnub.unsubscribe(channelName);
        isFirstMessage = true;
    }

    private void initializePolyline() {
        googleMap.clear();
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(Color.BLUE).width(10);
        googleMap.addPolyline(mPolylineOptions);

        mMarkerOptions = new MarkerOptions();
    }

    private void updatePolyline() {
        mPolylineOptions.add(mLatLng);
        googleMap.clear();
        googleMap.addPolyline(mPolylineOptions);
    }

    private void updateCamera() {
        googleMap
                .animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
    }

    private void updateMarker() {
//		if (!isFirstMessage) {
//			isFirstMessage = false;
//			mMarker.remove();
//		}
        mMarker = googleMap.addMarker(mMarkerOptions.position(mLatLng));
    }

    Callback subscribeCallback = new Callback() {

        @Override
        public void successCallback(String channel, Object message) {
            Log.d(PUBNUB_TAG, "Message Received: " + message.toString());
            JSONObject jsonMessage = (JSONObject) message;
            try {
                String id = jsonMessage.getString("ID");
                double mLat = jsonMessage.getDouble("Lat");
                double mLng = jsonMessage.getDouble("Lng");
                long timeToken = jsonMessage.getInt("TimeToken");
                mLatLng = new LatLng(mLat, mLng);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updatePolyline();
                    updateCamera();
                    updateMarker();
                }
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getActivity(), "REQUEST_LOCATION Allowed", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "REQUEST_LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setDefaultLocation(GoogleMap defaultLocation){
                LatLng horsens = new LatLng(55.866, 9.833);

        defaultLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(horsens, 13));

        defaultLocation.addMarker(new MarkerOptions()
                .title("Horsens")
                .position(horsens));

    }

    private void setFavLocation(GoogleMap favLocation){
        LatLng loc = new LatLng(favoriteItem.getLat(), favoriteItem.getLng());
        favLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
        favLocation.addMarker(new MarkerOptions()
                .title(favoriteItem.getName())
                .position(loc));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    private void collapseMap() {
        transparentView.setVisibility(View.GONE);
        spaceHeaderView.setVisibility(View.VISIBLE);
        if (googleMap != null && horsens != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(horsens, 11f), 1000, null);
        }
        homeListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        spaceHeaderView.setVisibility(View.GONE);
        transparentView.setVisibility(View.INVISIBLE);
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        homeListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelCollapsed(View panel) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View panel) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View panel) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GooglePlaceTask extends AsyncTask<String, String, String> {
        JSONObject obj = null;

//        @Override
//        protected Object doInBackground(Object[] params) {
//            try
//            {
//                tmp = makeCall("https://maps.googleapis.com/maps/api/place/search/json?location="
//                        + latitude + ","
//                        + longtitude + "&radius=100&sensor=true&key="
//                        + BuildConfig.);
//            }
//            catch (IOException e){
//                System.out.print(e.toString());
//            }
////            System.out.println("https://maps.googleapis.com/maps/api/place/search/json?location=" + latitude + "," + longtitude + "&radius=100&sensor=true&key=" + GOOGLE_KEY);
//            return "";
//        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject result = new JSONObject();
            URL url;
            HttpsURLConnection urlConnection;
            try{
                url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
                urlConnection = (HttpsURLConnection)url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                String parameters = "?location=" + 55.866 + "," + 9.833;
                parameters+="&radius=500";
                parameters+="&types=bus_station";
                parameters+="&key=AIzaSyCcwMBsRjVEwrvCxXDJ90lfJUm0askpA24";
                byte[] postData = parameters.getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                DataOutputStream data = new DataOutputStream(urlConnection.getOutputStream());
                data.write(postData);
                data.flush();
                data.close();

                StringBuilder sb= new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();

                if(HttpResult == HttpURLConnection.HTTP_OK){
                    String json;
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

            return result.toString();
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), "Loaded\n" + result, Toast.LENGTH_SHORT).show();
        }
    }

//    public static String makeCall(URL url) throws IOException{
//        BufferedReader reader = null;
//        String replyString = "";
//
//        try{
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
//            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            StringBuilder buf = new StringBuilder();
//            while((replyString=reader.readLine()) != null){
//                buf.append(replyString + "\n");
//            }
//        }
//        finally {
//            if (reader != null)
//            {
//                reader.close();
//            }
//        }
//    }

}

