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
import model.BusLineItem;
import model.HomeListView;
import utils.Constants;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusLinesFragment extends Fragment {

    public static final String TAG = "BusLinesFrag";
    public static final String ARG_PAGE = "ARG_PAGE";

    private HomeListView homeListView;
    private ListView busLines;
    private DatabaseHelper databaseHelper;

    public BusLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_lines, container, false);
        databaseHelper = DatabaseHelper.getInstance(getActivity());

        BusLineItem route1 = databaseHelper.getBusLine(Constants.route1);
        BusLineItem route2 = databaseHelper.getBusLine(Constants.route2);

        List<BusLineItem> busLinesInfo = new ArrayList<>();
        busLinesInfo.add(route1);
        busLinesInfo.add(route2);

        busLines = (ListView) view.findViewById(android.R.id.list);
        BusLinesListViewAdapter a = new BusLinesListViewAdapter(getActivity(), R.layout.bus_lines_list_view_row, busLinesInfo);
        busLines.setAdapter(a);
        busLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent busRoute = new Intent(getActivity(), RouteScreen.class);
                startActivity(busRoute);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
