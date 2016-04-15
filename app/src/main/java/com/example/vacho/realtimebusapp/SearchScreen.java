package com.example.vacho.realtimebusapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import adapter.FragmentPagerAdapter;
import adapter.PlaceArrayAdapter;
import fragment.LocationFragment;
import utils.DatabaseHelper;

public class SearchScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SearchScreen";

    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private FragmentPagerAdapter fragmentPagerAdapter;

    private GoogleApiClient googleApiClient;
    private PlaceArrayAdapter placeArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    LinearLayout contentSearchScreenMainLayout;
    TextView tabTitle;
    AutoCompleteTextView hintForSearchField;

    DatabaseHelper databaseHelper;

    public interface AutocompleteItemClickListener {
        public void onAutoCompleteItemClick(Place place);
    }

    AutocompleteItemClickListener autocompleteItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this) // RECREATED WITHIN onStart and onStop METHODS
                .addConnectionCallbacks(this)
                .build();
        databaseHelper = DatabaseHelper.getInstance(this);
        hintForSearchField = (AutoCompleteTextView) findViewById(R.id.atv_autocomplete_fav_item_address);
        if (hintForSearchField != null) {
            hintForSearchField.setOnItemClickListener(mAutocompleteClickListener);
        }
        placeArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        hintForSearchField.setAdapter(placeArrayAdapter);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        if (pagerSlidingTabStrip != null) {
            pagerSlidingTabStrip.setViewPager(viewPager);
        }

        if (pagerSlidingTabStrip != null) {
            contentSearchScreenMainLayout = ((LinearLayout) pagerSlidingTabStrip.getChildAt(0));
        }
        tabTitle = (TextView) contentSearchScreenMainLayout.getChildAt(0);
        tabTitle.setTextColor(Color.parseColor("#795548"));
        hintForSearchField.setHint("Where do you want to go?");

        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < contentSearchScreenMainLayout.getChildCount(); i++) {
                    tabTitle = (TextView) contentSearchScreenMainLayout.getChildAt(i);

                    if (i == position) {
                        tabTitle.setTextColor(Color.parseColor("#795548"));
                        hintForSearchField.setHint("Type your line number: ");
                    } else {
                        hintForSearchField.setHint("Where do you want to go?");
                        tabTitle.setTextColor(Color.GRAY);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
            Log.d(TAG, "Clicked " + place.getName() + " " + place.getAddress());
            ((AutocompleteItemClickListener) new LocationFragment()).onAutoCompleteItemClick(place);
        }
    };

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

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }
}
