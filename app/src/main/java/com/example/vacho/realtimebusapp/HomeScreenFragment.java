package com.example.vacho.realtimebusapp;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import java.util.ArrayList;

import adapter.CustomListViewAdapter;
import adapter.HeaderAdapter;
import model.BusStationInfo;
import model.LockableListView;
import model.LockableRecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener, HeaderAdapter.ItemClickListener, LocationListener {
    private static final String ARG_LOCATION = "arg.location";
    private View transparentView;
    private View whiteSpaceView;
    private HeaderAdapter headerAdapter;
    private LockableRecyclerView mListView;
    private LockableListView h;
    private View mTransparentHeaderView;
    private View mSpaceView;
    private LatLng mLocation;
    private Marker mLocationMarker;
    private SlidingUpPanelLayout slidingPaneLayout;

    private LatLng horsens;
    private boolean mIsNeedLocationUpdate = true;
    private GoogleMap googleMap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);


        h = (LockableListView) rootView.findViewById(android.R.id.list);
        h.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        slidingPaneLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_drawer);
        slidingPaneLayout.setEnableDragViewTouchEvents(true);

        //Getting the display Height
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);

        slidingPaneLayout.setPanelHeight(mapHeight); // you can use different height here
        slidingPaneLayout.setScrollableView(h, mapHeight);
        slidingPaneLayout.setPanelSlideListener(this);

        transparentView = rootView.findViewById(R.id.transparentView);
        whiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);

        mTransparentHeaderView = inflater.inflate(R.layout.transparent_header_view, h, false);
        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        collapseMap();

        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidingPaneLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                slidingPaneLayout.onPanelDragged(0);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.home_fragment);
            if (fragment != null) {
                fragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        whiteSpaceView.setVisibility(View.VISIBLE);

        BusStationInfo b[] = new BusStationInfo[]{
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

        h.addHeaderView(mTransparentHeaderView);
        h.setAdapter(new CustomListViewAdapter(getActivity(),R.layout.list_item,b));
        h.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slidingPaneLayout.collapsePane();
            }
        });


        // headerAdapter = new HeaderAdapter(getActivity(), testData, this);
        // mListView.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //  h.setLayoutManager(layoutManager);
        // mListView.setAdapter(headerAdapter);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        horsens = new LatLng(55.866, 9.833);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(horsens, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("Horsens")
                .position(horsens));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    private void collapseMap() {
        if (headerAdapter != null) {
            headerAdapter.showSpace();
        }
        transparentView.setVisibility(View.GONE);
        mSpaceView.setVisibility(View.VISIBLE);
        // mTransparentHeaderView.setVisibility(View.GONE);
        if (googleMap != null && horsens != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(horsens, 11f), 1000, null);
        }
        h.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (headerAdapter != null) {
            headerAdapter.hideSpace();
        }
        mSpaceView.setVisibility(View.GONE);
        transparentView.setVisibility(View.INVISIBLE);
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        h.setScrollingEnabled(false);
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
    }


    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToSetLocation(latLng);
    }

    private void moveToSetLocation(LatLng latLng) {
        moveToNewLocation(latLng, true);
    }

    private void moveToNewLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (googleMap != null && moveCamera) {
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        });
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
    public void onItemClicked(int position) {
        slidingPaneLayout.collapsePane();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
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

