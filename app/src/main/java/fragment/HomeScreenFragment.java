package fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.BuildConfig;
import com.example.vacho.realtimebusapp.HomeScreen;
import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import adapter.CustomListViewAdapter;
import model.BusStationInfo;
import model.FavoriteItem;
import model.HomeListView;
import service.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private static final String ARG_LOCATION = "arg.location";

    private View transparentView;
    private View whiteSpaceView;
    private View transparentHeaderView;
    private View spaceHeaderView;

    private HomeListView homeListView;
    private SlidingUpPanelLayout slidingPaneLayout;
    private LatLng horsens;
    private GoogleMap googleMap;

    private static final String TAG = "HomeScreenFrag";
    public int pos = 0;
    FavoriteItem favoriteItem;
    GPSTracker gpsTracker;

    private Pubnub pubnub;
    private Activity mActivity;
    private Object m;
//    private boolean tracking;
    FavoriteItem trackedItem;

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
//        tracking = false;
        pubnub = getPubnub();
        subscribeToChannel();

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
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        if(!HomeScreen.tracking)
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

    public Pubnub getPubnub()
    {
        if(null == pubnub)
        {
            pubnub = new Pubnub(BuildConfig.PUBLISH_KEY, BuildConfig.SUBSCRIBE_KEY, false);
        }
        return pubnub;
    }

    public void subscribeToChannel()
    {
        try {
            pubnub.subscribe("my_channel", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            pubnub.publish("my_channel", "Hello from " + Build.MODEL, new Callback() {
                            });
                            Log.d(TAG, "connectCallback:" + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            Log.d(TAG, "disconnectCallback:" + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            Log.d(TAG, "reconnectCallback:" + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            Log.d(TAG, "successCallback:" + message.toString());
                            try{
                                if(message instanceof JSONObject)
                                {
                                    m = message;
                                    final JSONObject messageJSON = (JSONObject) message;
                                    if(messageJSON.has("ID"))
                                    {
                                        final String id = messageJSON.getString("ID");
                                        final double lat = messageJSON.getDouble("Lat");
                                        final double lng = messageJSON.getDouble("Lng");
                                        final long timeToken = messageJSON.getInt("TimeToken");
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d(TAG + " IN ", "ID: " + id + " Lat: " + lat + " Lng " + lng + " TimeToken: " + timeToken);
                                                if(HomeScreen.tracking)
                                                {
                                                    googleMap.clear();
                                                    trackMarker(googleMap, id, lat, lng);
                                                }
                                            }
                                        });

                                        Gson gson = new Gson();
                                        gson.toJson(message);

                                    }
                                    else
                                    {
                                        Log.d(TAG + " IN ", message.toString());
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e(TAG, "exception while processing presence event", e);
                            }

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                            Log.d(TAG, "errorCallback: " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

    public void trackMarker(GoogleMap map, String id, double lat, double lng)
    {
        LatLng loc = new LatLng(lat, lng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
        map.addMarker(new MarkerOptions()
                    .title(id)
                    .position(loc));
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
}

