package com.example.vacho.realtimebusapp;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback, SlidingUpPanelLayout.PanelSlideListener {

    private SlidingUpPanelLayout slidingPaneLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);

        // Getting the display Height
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        slidingPaneLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_drawer);
        slidingPaneLayout.setPanelHeight(height/2);
        slidingPaneLayout.setPanelSlideListener(this);

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
    public void onMapReady(GoogleMap googleMap) {
        LatLng horsens = new LatLng(55.866, 9.833);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(horsens, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("Horsens")
                .position(horsens));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelCollapsed(View panel) {

    }

    @Override
    public void onPanelExpanded(View panel) {

    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    @Override
    public void onPanelHidden(View panel) {

    }
}

