package fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.CustomListViewAdapter;
import async_tasks.AsyncResponseBusStationsListener;
import async_tasks.AsyncResponseDirectionsListener;
import async_tasks.GetDirectionsTask;
import async_tasks.GetNearestBusStations;
import model.BusLineItem;
import model.HomeListView;
import model.LocationItem;
import model.google_items.Leg;
import model.google_items.Place;
import model.google_items.Route;
import model.google_items.Step;
import utils.DatabaseHelper;
import utils.PubNubManager;
import utils.TaskParameters;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener, AsyncResponseBusStationsListener, AsyncResponseDirectionsListener {

    private View transparentView;
    private View whiteSpaceView;
    private View transparentHeaderView;
    private View spaceHeaderView;

    private static final int MODE_HOME = 10;
    private static final int MODE_LOCATION = 11;
    private static final int MODE_ROUTE = 12;
    private static int DISPLAY_MODE = MODE_HOME;

    private DatabaseHelper databaseHelper;

    private HomeListView homeListView;
    private SlidingUpPanelLayout slidingPaneLayout;
    private LatLng horsens;
    private LatLng currentLocation;
    private final LatLng trafikTerminal = new LatLng(55.8629951, 9.8365588);
    private final LatLng via = new LatLng(55.8695091, 9.8858728);
    private GoogleMap googleMap;
    private static final int REQUEST_LOCATION = 0;
    private static final int RESULT_OK = 100;

    protected LocationManager locationManager;
    private boolean isGPSEnabled;
    private LocationListener locationListener;
    private Location location;

    private static final String TAG = "HomeScreenFrag";
    public int pos = 0;
    LocationItem favoriteItem;

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

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_screen, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();

        DISPLAY_MODE = MODE_HOME;
        Log.d("STATE", " onCreateView DISPLAY_MODE" + DISPLAY_MODE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String fav_item_name = bundle.getString("fav_item_name");
            String fav_item_address = bundle.getString("fav_item_address");
            double fav_item_lat = bundle.getDouble("fav_item_lat");
            double fav_item_lng = bundle.getDouble("fav_item_lng");
            float fav_item_zoom = bundle.getFloat("fav_item_zoom");
            boolean fav_item_favourited = bundle.getBoolean("fav_item_favourited");
            if (fav_item_name != null &&
                    fav_item_address != null &&
                    fav_item_lat != 0 &&
                    fav_item_lng != 0 &&
                    fav_item_zoom != 0) {
                favoriteItem = new LocationItem(fav_item_name, fav_item_address, fav_item_lat, fav_item_lng, fav_item_zoom, fav_item_favourited);
                Toast.makeText(getActivity(), "fav_item_name " + favoriteItem.getName(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "fav_item_name " + favoriteItem.getName() + " isFavourited = " + favoriteItem.isFavourited());
            }
//            getActivity().getActionBar().setTitle(R.string.title_home);
        }

        databaseHelper = DatabaseHelper.getInstance(getActivity());

        homeListView = (HomeListView) v.findViewById(android.R.id.list);
        homeListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        slidingPaneLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_drawer);
        slidingPaneLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);

        //When map is expanded
        slidingPaneLayout.setPanelHeight(mapHeight); // you can use different height here
        slidingPaneLayout.setScrollableView(homeListView, mapHeight);
        slidingPaneLayout.setPanelSlideListener(this);

        transparentView = v.findViewById(R.id.transparentView);
        whiteSpaceView = v.findViewById(R.id.whiteSpaceView);

        transparentHeaderView = inflater.inflate(R.layout.transparent_header_view, homeListView, false);
        spaceHeaderView = transparentHeaderView.findViewById(R.id.space);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
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
        };

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
//         setHasOptionsMenu(true); // For Handling Fragment calls to menu items

        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_fragment);
        fragment.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        whiteSpaceView.setVisibility(View.VISIBLE);
        String dot = "\u2022 Bullet";

        homeListView.addHeaderView(transparentHeaderView);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        btnTrack = menu.findItem(R.id.action_tracking);
//    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "Map Ready");

        homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slidingPaneLayout.collapsePane();
                LocationItem item = (LocationItem) parent.getAdapter().getItem(position);
                Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_SHORT).show();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(item.getLat(), item.getLng())));
            }
        });
//        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            else {
                this.googleMap.setMyLocationEnabled(true);
                Log.d(TAG, "MyLocation: " + googleMap.isMyLocationEnabled());
            }
        }

        googleMap.setMyLocationEnabled(true);
//        Log.d(TAG, "MyLocation: " + googleMap.isMyLocationEnabled());
//
////        this.googleMap.setTrafficEnabled(true);
////        this.googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
////        googleMap.setPadding(20, 20, 20, 20);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        Log.d(TAG, "isCompassEnabled: " + googleMap.getUiSettings().isCompassEnabled());
        Log.d(TAG, "isMyLocationButtonEnabled: " + googleMap.getUiSettings().isMyLocationButtonEnabled());

        this.googleMap = googleMap;

//        if(!requestingLocationUpdates)
//        {

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("fromFrag")) {
                Toast.makeText(getActivity(), extras.getString("fromFrag"), Toast.LENGTH_SHORT).show();
                DISPLAY_MODE = MODE_LOCATION;
                drawLocation(extras);
            } else if (extras.containsKey("SerializableRoute")) {
                if (extras.getSerializable("SerializableRoute") != null) {
                    Log.d(TAG, extras.getSerializable("SerializableRoute").toString());
                    DISPLAY_MODE = MODE_ROUTE;
                    drawRoute(extras);
                }
            }
        }

        if (favoriteItem == null) {
            setDefaultLocation(googleMap);
        } else {
            setFavLocation(googleMap);
        }
        Log.d("STATE", " onMapReady DISPLAY_MODE" + DISPLAY_MODE);
    }

    private void drawLocation(Bundle extras) {
        Log.d(TAG, "drawLocation started");
        this.googleMap.clear();
        ArrayList arrayList = getActivity().getIntent().getParcelableArrayListExtra("custom_data_list");
        LocationItem i = (LocationItem) arrayList.get(0);
        final List<LocationItem> busStationInfos = new ArrayList<>();
        busStationInfos.add(new LocationItem(i.getName(), i.getAddress(), i.getLat(), i.getLng()));
        homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, busStationInfos));
        this.googleMap.addMarker(new MarkerOptions().title(i.getName()).position(new LatLng(i.getLat(), i.getLng())));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(i.getLat(), i.getLng())));
        Log.d(TAG, i.toString());
    }

    private void drawRoute(Bundle extras) {
        Log.d(TAG, "drawRoute started");
        this.googleMap.clear();
        startFollowingLocation();
        BusLineItem busLineItem = (BusLineItem) extras.getSerializable("SerializableRoute");
        PolylineOptions options = new PolylineOptions();
        List<LatLng> busStations = new ArrayList<>();
        List<LatLng> waypoints = new ArrayList<>();
        List<LocationItem> infos = new ArrayList<>();
        // Adding bus Stations and Bus stations
        for (LocationItem item : busLineItem.getBusStations()) {
            busStations.add(new LatLng(item.getLat(), item.getLng()));
            infos.add(new LocationItem(item.getName(), item.getAddress(), item.getLat(), item.getLng()));
            this.googleMap.addMarker(new MarkerOptions()
                    .title(item.getName())
                    .position(new LatLng(item.getLat(), item.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp)));
        }
        // Setting all waypoints
        for (int i = 0; i < busLineItem.getBusStationOrder().length; i++) {
            for (int j = 0; j < busLineItem.getBusStations().size(); j++) {
                if (busLineItem.getBusStations().get(j).getId() == busLineItem.getBusStationOrder()[i]) {
                    waypoints.add(new LatLng(busLineItem.getBusStations().get(j).getLat(), busLineItem.getBusStations().get(j).getLng()));
                }
            }
        }

        homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, infos));

        Log.d(TAG, waypoints.toString());
        Log.d(TAG, waypoints.size() + "");

        TaskParameters getDirections = new TaskParameters(trafikTerminal, trafikTerminal);
        getDirections.setKey(BuildConfig.SERVER_KEY);
        getDirections.setOptimize(true);
        getDirections.setTravelMode(TaskParameters.TravelMode.DRIVING);
        getDirections.setWaypoints(waypoints);
        GetDirectionsTask task = new GetDirectionsTask();
        task.delegate = this;
        task.execute(getDirections);
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.action_tracking:
//                Log.d(TAG, "'Tracking' Button Pressed");
//                requestingLocationUpdates = !requestingLocationUpdates;
//                if (requestingLocationUpdates) {
//                    startFollowingLocation();
//                    btnTrack.setTitle("Tracking");
//                }
//                if (!requestingLocationUpdates) {
//                    stopFollowingLocation();
//                    btnTrack.setTitle("Track");
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    private void setDefaultLocation(GoogleMap defaultLocation) {
        horsens = new LatLng(55.866, 9.833);
        TaskParameters parameters = new TaskParameters();
        parameters.setLocation(horsens);
        defaultLocation.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                else {
                    Log.d(TAG, "MyLocation: " + googleMap.isMyLocationEnabled());
                }
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                defaultLocation.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                defaultLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                defaultLocation.addMarker(new MarkerOptions()
                        .title("Current Location")
                        .position(latLng));
                //--------------------------
                currentLocation = latLng;
                parameters = new TaskParameters(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        } else {
            defaultLocation.moveCamera(CameraUpdateFactory.newLatLng(horsens));
            defaultLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(horsens, 15));
            defaultLocation.addMarker(new MarkerOptions()
                    .title("Horsens")
                    .position(horsens));
            parameters = new TaskParameters(horsens);
        }
        Log.d("STATE", " setDefaultLocation DISPLAY_MODE" + DISPLAY_MODE);
        Log.d(TAG, parameters.toString());
        if (DISPLAY_MODE == MODE_HOME) {
            GetNearestBusStations task = new GetNearestBusStations();
            task.delegate = this;
            task.execute(parameters);
        }
    }

    private void setFavLocation(GoogleMap favLocation) {
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
    public void onLocationsTaskEndWithResult(int success) {
        switch (success) {
            case 0:
                Log.d(TAG, "onLocationsTaskEndWithResult Success = " + success);
                break;
            case 1:
                Log.d(TAG, "onLocationsTaskEndWithResult Success = " + success);
                break;
            default:
                Log.d(TAG, "onLocationsTaskEndWithResult Success = " + success);
                break;
        }
    }

    @Override
    public void onLocationsProcessFinish(final List<Place> places) {

        final List<LocationItem> busStationInfos = new ArrayList<>();

        if (places != null) {
            Log.d(TAG, places.toString());
            for (Place place : places) {
                busStationInfos.add(new LocationItem(place.getName(), place.getVicinity(), place.getLocation().latitude, place.getLocation().longitude));
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, busStationInfos));
                    for (Place p : places) {
                        googleMap.addMarker(new MarkerOptions()
                                .title(p.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_36dp))
                                .position(p.getLocation()));
                    }
                }
            });
        }
    }

    @Override
    public void onDirectionsTaskEndWithResult(int success) {
        switch (success) {
            case 0:
                Log.d(TAG, "onDirectionsTaskEndWithResult Success = " + success);
                break;
            case 1:
                Log.d(TAG, "onDirectionsTaskEndWithResult Success = " + success);
                break;
            default:
                Log.d(TAG, "onDirectionsTaskEndWithResult Success = " + success);
                break;
        }
    }

    @Override
    public void onDirectionsProcessFinish(List<Route> places) {
        Log.d("TaskFinished", "directions finished");
        if (places != null) {

            Log.d(TAG, places.toString());

            ArrayList<LatLng> points;
            final PolylineOptions polyLineOptions = new PolylineOptions();
            // traversing through routes

            for (int i = 0; i < places.size(); i++) {
                points = new ArrayList<>();
                Route path = places.get(i);
                Log.d(TAG, path.toString());
                for (Leg leg : path.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        for (LatLng position : step.getPoints()) {
                            points.add(position);
                        }
                    }
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, busStationInfos));
                    googleMap.addPolyline(polyLineOptions);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DISPLAY_MODE == MODE_ROUTE) {
            startFollowingLocation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DISPLAY_MODE == MODE_ROUTE) {
            stopFollowingLocation();
        }
    }
}