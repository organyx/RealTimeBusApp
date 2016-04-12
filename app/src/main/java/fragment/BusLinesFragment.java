package fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;
import com.example.vacho.realtimebusapp.RouteScreen;
import com.example.vacho.realtimebusapp.SearchScreen;

import adapter.BusListViewAdapter;
import model.BusStationInfo;
import model.HomeListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusLinesFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private HomeListView homeListView;
    private ListView busLines;

    public BusLinesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_lines, container, false);

        BusStationInfo busLinesInfo[] = new BusStationInfo[]{
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "Bus Line 1"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "2"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "3"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "4"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "5"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "6"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "7"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "8"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "9"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "10"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "12"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "13"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "14"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "15"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "16"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "17"),
                new BusStationInfo(R.drawable.ic_directions_bus_black_24dp, "18")

        };

        busLines = (ListView) view.findViewById(android.R.id.list);
        BusListViewAdapter a = new BusListViewAdapter(getActivity(), R.layout.bus_lines_list_view_row, busLinesInfo);
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
