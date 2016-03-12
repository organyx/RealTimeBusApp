package fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import model.FavoriteItem;
import service.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeScreenFrag";
    public int pos = 0;
    FavoriteItem favoriteItem;
    GPSTracker gpsTracker;

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
    public void onMapReady(GoogleMap googleMap) {

        if (favoriteItem == null)
        {
            setDefaultLocation(googleMap);
        }
        else
        {
            setFavLocation(googleMap);
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
}

