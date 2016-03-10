package com.example.vacho.realtimebusapp;


import android.app.Fragment;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import java.util.ArrayList;

import adapter.HeaderAdapter;
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
    private LatLng mLocation;
    private Marker mLocationMarker;
    private SlidingUpPanelLayout slidingPaneLayout;

    private LatLng horsens;
    private boolean mIsNeedLocationUpdate = true;
    private GoogleMap googleMap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);


        mListView = (LockableRecyclerView) rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);


        transparentView = rootView.findViewById(R.id.transparentView);
        whiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);


        slidingPaneLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_drawer);
        slidingPaneLayout.setEnableDragViewTouchEvents(true);

        //Getting the display Height
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);

        slidingPaneLayout.setPanelHeight(height / 3); // you can use different height here
        slidingPaneLayout.setScrollableView(mListView, mapHeight);
        slidingPaneLayout.setPanelSlideListener(this);

        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                slidingPaneLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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

        ArrayList<String> testData = new ArrayList<String>(50);
        for (int i = 0; i < 50; i++) {
            testData.add("Item " + i);
        }
        whiteSpaceView.setVisibility(View.VISIBLE);


        headerAdapter = new HeaderAdapter(getActivity(), testData, this);
        mListView.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(headerAdapter);

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
        if (googleMap != null && horsens != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(horsens, 11f), 1000, null);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (headerAdapter != null) {
            headerAdapter.hideSpace();
        }
        transparentView.setVisibility(View.INVISIBLE);
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
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

