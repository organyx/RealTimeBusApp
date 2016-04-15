package com.example.vacho.realtimebusapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import fragment.AboutScreenFragment;
import fragment.EditFavFragment;
import fragment.FavoritesScreenFragment;
import fragment.HomeScreenFragment;
import fragment.IntermediateMapFragment;
import fragment.NavigationDrawerFragment;
import utils.DatabaseHelper;


public class HomeScreen extends AppCompatActivity implements NavigationDrawerFragment.FragmentDrawerListener, GoogleApiClient.OnConnectionFailedListener, EditFavFragment.NoticeDialogListener {

    private static final String TAG = "HomeScreen";
    public static final String SAVED_PREFERENCES = "SAVED_PREFERENCES";
    public static final String SAVED_FIRST_START = "SAVED_FIRST_START";
    private NavigationDrawerFragment navigationDrawerFragment;
    public static GoogleApiClient googleApiClient;
    FloatingActionButton fab;

    private FavoritesScreenFragment fsf;

    private boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        tracking = false;

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        navigationDrawerFragment.setDrawerListener(this);

        fab = (FloatingActionButton) this.findViewById(R.id.fab);


        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        displayView(0);

        // THIS BLOCK EXECUTES ONLY ON THE 1ST LAUNCH OF THE APP
        loadFirstTime();
        if (firstStart) {
//            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
            databaseHelper.populateBusLineBusStations();
            firstStart = false;
            saveFirstTime();
        }
    }

    View.OnClickListener clickSearchIcon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent searchIntent = new Intent(HomeScreen.this, SearchScreen.class);
            startActivity(searchIntent);
        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);

    }

    private void displayView(int position) {

        FragmentManager fm = getFragmentManager();

//            fsf = FavoritesScreenFragment.newInstance(position);
//            Bundle args = new Bundle();
//            args.putInt(FavoritesScreenFragment.IMAGE_RES, position);
//            fsf.setArguments(args);
//

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fm.beginTransaction().replace(R.id.fragment_container, new HomeScreenFragment()).commit();
                title = getString(R.string.title_home);
                fab.setOnClickListener(clickSearchIcon);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_black_24dp));
                break;
            case 1:
                fm.beginTransaction().replace(R.id.fragment_container, new FavoritesScreenFragment()).commit();

                title = getString(R.string.title_favourites);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_dark_plus_24dp));
                break;
            case 2:
                fm.beginTransaction().replace(R.id.fragment_container, new AboutScreenFragment()).commit();
                title = "About";
                break;
            default:
                break;
        }

        getSupportActionBar().setTitle(title);
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
    protected void onResume() {
        super.onResume();
        loadFirstTime();
    }

    private boolean loadFirstTime() {
        SharedPreferences prefs = getSharedPreferences(SAVED_PREFERENCES, MODE_PRIVATE);
        firstStart = prefs.getBoolean(SAVED_FIRST_START, true);
        Log.d(TAG, "Loaded value: " + firstStart);
        return firstStart;
    }

    private void saveFirstTime() {
        SharedPreferences prefs = getSharedPreferences(SAVED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SAVED_FIRST_START, firstStart);
        Log.d(TAG, "Saved value: " + firstStart);
        editor.apply();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String edited_name, String edited_address) {
        Log.d(TAG, "CLICKED: OK");
        Log.d(TAG, edited_address + " " + edited_name);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "CLICKED: CANCEL");
    }
}
