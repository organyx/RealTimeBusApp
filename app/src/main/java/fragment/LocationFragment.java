package fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.CustomListViewAdapter;
import adapter.FavoriteListAdapter;
import model.BusStationInfo;
import model.HomeListView;
import model.LocationItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

//    private RecyclerView recyclerView;
//    private FavoriteListAdapter favoriteListAdapter;
//    private RecyclerView.LayoutManager layoutManager;

    private HomeListView homeListView;
    private ImageView imageView;

    private BusStationInfo busStationInfos[];

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        homeListView = (HomeListView) rootView.findViewById(android.R.id.list);
        imageView = (ImageView) rootView.findViewById(R.id.vert_search_screen);
        // recyclerView = (RecyclerView) rootView.findViewById(R.id.number1);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), imageView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_clear_recent_history, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                getActivity(),
                                "Under Development",
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        busStationInfos = new BusStationInfo[]{
                new BusStationInfo("Torvet", "Torvet 26"),
                new BusStationInfo("Borgergade", "Borgergade 3,3TV"),
                new BusStationInfo("Rådhuse", "Rådhuse 4A")
        };


        homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, busStationInfos));
        homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

//        LocationItem item = new LocationItem("Torvet", "Torvet 26");
//        List<LocationItem> list = new ArrayList<LocationItem>();
//        list.add(0,item);
//
//
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//
//        favoriteListAdapter = new FavoriteListAdapter(getActivity(), list);
//        recyclerView.setAdapter(favoriteListAdapter);

    }
}
