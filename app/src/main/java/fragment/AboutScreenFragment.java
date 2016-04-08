package fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.vacho.realtimebusapp.R;

import model.BusLineItem;
import utils.Constants;
import utils.DatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutScreenFragment extends Fragment {

    private static final String TAG = "AboutScreenFragment";
    private MenuItem btnTrack;

    private DatabaseHelper databaseHelper;
    private BusLineItem route;

    public AboutScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_screen, container, false);

        setHasOptionsMenu(true); // For Handling Fragment calls to menu items

        databaseHelper = new DatabaseHelper(getActivity());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        btnTrack = menu.findItem(R.id.action_tracking);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_tracking:
                Log.d(TAG, "'Tracking' Button Pressed");
                route = databaseHelper.getBusLine(Constants.route1);
                Log.d(TAG, route.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
