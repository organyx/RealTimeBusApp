package com.example.vacho.realtimebusapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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
import fragment.FavoritesScreenFragment;
import fragment.HomeScreenFragment;
import fragment.NavigationDrawerFragment;


public class HomeScreen extends AppCompatActivity implements NavigationDrawerFragment.FragmentDrawerListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HomeScreen";
    private NavigationDrawerFragment navigationDrawerFragment;
    public static GoogleApiClient googleApiClient;

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

        FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(clickSearchIcon);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        displayView(0);
    }

    View.OnClickListener clickSearchIcon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent searchIntent = new Intent(HomeScreen.this,SearchScreen.class);
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
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fm.beginTransaction().replace(R.id.fragment_container, new HomeScreenFragment()).commit();
                title = getString(R.string.title_home);
                break;
            case 1:
                fm.beginTransaction().replace(R.id.fragment_container, new FavoritesScreenFragment()).commit();
                title = getString(R.string.title_favourites);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
}
