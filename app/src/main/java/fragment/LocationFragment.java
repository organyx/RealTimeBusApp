package fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.vacho.realtimebusapp.HomeScreen;
import com.example.vacho.realtimebusapp.R;
import com.example.vacho.realtimebusapp.SearchScreen;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

import adapter.CustomListViewAdapter;
import model.HomeListView;
import model.LocationItem;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements SearchScreen.AutocompleteItemClickListener {

    public static final String TAG = "LocationFrag";
    public static final String ARG_PAGE = "ARG_PAGE";

    private static HomeListView homeListView;
    private ImageView imageView;

    private static List<LocationItem> busStationInfos;
    private static DatabaseHelper databaseHelper;
    private static Context context;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        databaseHelper = DatabaseHelper.getInstance(getActivity());
        context = getContext();
        homeListView = (HomeListView) rootView.findViewById(android.R.id.list);
        imageView = (ImageView) rootView.findViewById(R.id.vert_search_screen);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), imageView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_clear_recent_history, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        databaseHelper.clearRecentHistory();
                        homeListView.setAdapter(new CustomListViewAdapter(getActivity(), R.layout.list_item, new ArrayList<LocationItem>()));
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

        final List<LocationItem> locationsInfo = databaseHelper.getRecentHistory();
        busStationInfos = new ArrayList<>();
        for (LocationItem item : locationsInfo) {
            busStationInfos.add(new LocationItem(item.getName(), item.getAddress()));
        }
        updateAdapter(busStationInfos);
        homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                Intent intent = new Intent(getActivity(), HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("fromFrag", "From Location Fragment");
                LocationItem i = locationsInfo.get(position);
                ArrayList<LocationItem> dataList = new ArrayList<>();
                dataList.add(i);
                intent.putParcelableArrayListExtra("custom_data_list", dataList);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAutoCompleteItemClick(Place place) {
        LocationItem item2 = new LocationItem(place.getName().toString(), place.getAddress().toString(), place.getLatLng().latitude, place.getLatLng().longitude, 0, false);
        Log.d(TAG, "Item clicked: " + item2.getName() + " " + item2.getAddress());

        databaseHelper.addNewLocation(item2.getName(), item2.getAddress(), item2.getLat(), item2.getLng(), 0, 0);
        databaseHelper.updateLocationVisits(item2.getName(), 1);
        List<LocationItem> locationsInfo = databaseHelper.getRecentHistory();
        busStationInfos = new ArrayList<>();
        for (LocationItem item : locationsInfo) {
            busStationInfos.add(new LocationItem(item.getName(), item.getAddress()));
        }
        updateAdapter(busStationInfos);
    }

    private void updateAdapter(List<LocationItem> i) {
        if (homeListView != null)
            if (context != null)
                homeListView.setAdapter(new CustomListViewAdapter(context, R.layout.list_item, i));
    }
}
