package fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.HomeScreen;
import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import adapter.PlaceArrayAdapter;
import utils.DatabaseHelper;

/**
 * Created by Aleks on 09-Mar-16.
 * Fragment to help manually select Favourite Location.
 */
public class IntermediateMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "IntrMapFragment";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView atv_fav_item_address;
    private TextView tv_fav_item_name;
    private GoogleApiClient googleApiClient;
    private PlaceArrayAdapter placeArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private Button btn_add_fav_item;
    private Place placeToAdd;
    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intermediate_map, container, false);

//
//        // set menu icon as back arrow
//        Fragment f = getActivity().getFragmentManager().findFragmentById(R.id.fragment_container);
//        if(f instanceof IntermediateMapFragment){
//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            if(toolbar != null){
//              //  toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
//
//                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FragmentManager fm = getFragmentManager();
//                        fm.beginTransaction().replace(R.id.fragment_container, new FavoritesScreenFragment()).commit();
//                        ((AppCompatActivity)getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//                        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//                    }
//                });
//            }
//        }

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this) // RECREATED WITHIN onStart and onStop METHODS
                .addConnectionCallbacks(this)
                .build();
        atv_fav_item_address = (AutoCompleteTextView) v.findViewById(R.id
                .atv_autocomplete_fav_item_address);
        atv_fav_item_address.setThreshold(1);
        tv_fav_item_name = (EditText) v.findViewById(R.id.et_fav_item_name);

        atv_fav_item_address.setOnItemClickListener(mAutocompleteClickListener);
        placeArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        atv_fav_item_address.setAdapter(placeArrayAdapter);

        btn_add_fav_item = (Button) v.findViewById(R.id.btn_add_fav_item);
        btn_add_fav_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity());
                databaseHelper.addNewLocation(placeToAdd.getName().toString(), placeToAdd.getAddress().toString(), placeToAdd.getLatLng().latitude, placeToAdd.getLatLng().longitude, 0, 1);

                FavoritesScreenFragment favoritesScreenFragment = new FavoritesScreenFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, favoritesScreenFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.f_intermediate_map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            tv_fav_item_name.setText(Html.fromHtml(place.getName() + ""));
            addMarker(place);
        }
    };

    private void addMarker(Place place) {
        String name = place.getName() + "";
        LatLng newLoc = place.getLatLng();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 13));
        map.addMarker(new MarkerOptions().title(name).position(newLoc));

        placeToAdd = place;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placeArrayAdapter.setGoogleApiClient(googleApiClient);
        Log.i(TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        placeArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
}
