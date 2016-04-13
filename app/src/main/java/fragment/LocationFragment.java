package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vacho.realtimebusapp.R;
import com.example.vacho.realtimebusapp.RouteScreen;

import java.util.ArrayList;
import java.util.List;

import adapter.BusLinesListViewAdapter;
import adapter.LocationListViewAdapter;
import model.LocationItem;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    public static final String TAG = "LocationFrag";
    public static final String ARG_PAGE = "ARG_PAGE";

    private ListView locations;
    private DatabaseHelper databaseHelper;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_lines, container, false);
        databaseHelper = DatabaseHelper.getInstance(getActivity());

        List<LocationItem> locationsInfo;

        locationsInfo = databaseHelper.getAllLocations();


        this.locations = (ListView) view.findViewById(android.R.id.list);
        LocationListViewAdapter a = new LocationListViewAdapter(getActivity(), R.layout.location_list_view_row, locationsInfo);
        this.locations.setAdapter(a);
        this.locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent loc = new Intent(getActivity(), RouteScreen.class);
                startActivity(loc);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
