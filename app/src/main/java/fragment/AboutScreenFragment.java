package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;

import model.BusLineItem;
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
        View view = inflater.inflate(R.layout.fragment_about_screen, container, false);
        ImageView v = (ImageView) view.findViewById(R.id.omg);
        if (v != null) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "DODO", Toast.LENGTH_LONG).show();
                }
            });
        }

        setHasOptionsMenu(true); // For Handling Fragment calls to menu items

//        databaseHelper = new DatabaseHelper(getActivity());
        databaseHelper = DatabaseHelper.getInstance(getActivity());
        return view;
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
//                route = databaseHelper.getBusLine(Constants.route1);
//                Log.d(TAG, route.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
