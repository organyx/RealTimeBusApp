package fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import adapter.CustomListViewAdapter;
import model.BusStationInfo;
import model.FavoriteItem;
import model.HomeListView;
import service.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener {

    private static final String ARG_LOCATION = "arg.location";

    private View transparentView;
    private View whiteSpaceView;
    private View transparentHeaderView;
    private View spaceHeaderView;

    private HomeListView homeListView;
    private SlidingUpPanelLayout slidingPaneLayout;
    private LatLng horsens;
    private GoogleMap googleMap;

    protected LocationManager locationManager;
    private boolean isGPSEnabled;
    private LocationListener locationListener;
    private Location location;

    private static final String TAG = "HomeScreenFrag";
    public int pos = 0;
    FavoriteItem favoriteItem;


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


       // expandMap();

        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidingPaneLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                slidingPaneLayout.onPanelDragged(0);
            }
        });
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
        String dot = "\u2022 Bullet";

        BusStationInfo busStationInfos[] = new BusStationInfo[]{
                new BusStationInfo(R.drawable.ic_flag_24dp, "Åboulevarden", "3 min a pe " + "\u2022" + " 15, 3A"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "Gasve", "3 min a pe " + "\u2022" + " 1, 3"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "Sundhedshuset", "5 min a pe " + "\u2022" + " 11, 3A, 6G"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "V. Berings Plads", "8 min a pe " + "\u2022" + " 15"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "Hybenvej", "9 min a pe " + "\u2022" + " 3A"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "VIA, Chr. M. Østergårdsve", "9 min a pe " + "\u2022" + " 6G"),
                new BusStationInfo(R.drawable.ic_flag_24dp, "Rådhuse", "12 min a pe " + "\u2022" + " 15, 4A")
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

        if (favoriteItem == null) {
            setDefaultLocation(googleMap);
        } else {
            setFavLocation(googleMap);
        }
    }

    private void setDefaultLocation(GoogleMap defaultLocation) {
        LatLng horsens = new LatLng(55.866, 9.833);

        defaultLocation.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                defaultLocation.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                defaultLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                defaultLocation.addMarker(new MarkerOptions()
                        .title("Current Location")
                        .position(latLng));

            }
        }else {
            defaultLocation.moveCamera(CameraUpdateFactory.newLatLng(horsens));
            defaultLocation.moveCamera(CameraUpdateFactory.newLatLngZoom(horsens, 15));
            defaultLocation.addMarker(new MarkerOptions()
                    .title("Horsens")
                    .position(horsens));
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

}

