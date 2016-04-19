package fragment;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;

import java.util.List;

import adapter.FavoriteListAdapter;
import model.LocationItem;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesScreenFragment extends Fragment implements EditFavFragment.NoticeDialogListener {

    private static final String TAG = "FavoritesFrag";

    DatabaseHelper db;
    private FloatingActionButton addButton;
    private PopupMenu popupMenu;
    private RecyclerView recyclerView;
    private FavoriteListAdapter favoriteListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String temp;
    private int editableItemID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_favourites_screen, container, false);
        db = DatabaseHelper.getInstance(getActivity());

        final List<LocationItem> list = db.getAllFavourites();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_fav_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        favoriteListAdapter = new FavoriteListAdapter(getActivity(), list);
        recyclerView.setAdapter(favoriteListAdapter);

        favoriteListAdapter.setOnItemClickListener(new FavoriteListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View itemView, int position) {
                final LocationItem editable_item = db.getAllFavourites().get(position);
                temp = editable_item.getName();
                popupMenu = new PopupMenu(getActivity(), itemView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_favourites, popupMenu.getMenu());

                //registering popup with OnMenuItemClickListener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Edit")) {
                            editableItemID = editable_item.getId();
                            openEditFavDialog(editable_item);
                        } else {
                            db.updateLocationFavourite(temp, false);
                            favoriteListAdapter.notifyItemRemoved(list.size() - 1);
                            favoriteListAdapter.notifyDataSetChanged();
                            favoriteListAdapter = new FavoriteListAdapter(getActivity(), db.getAllFavourites());
                            recyclerView.setAdapter(favoriteListAdapter);
                            favoriteListAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onClick(View itemView) {
                Toast.makeText(getActivity(), " DOODO!", Toast.LENGTH_SHORT).show();
            }
        });

        addButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        addButton.show();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntermediateMapFragment mapFragment = new IntermediateMapFragment();
                Bundle args = new Bundle();
                mapFragment.setArguments(args);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                addButton.hide();
//                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//                if (toolbar != null) {
//                    toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
//                }
            }
        });

        return rootView;
    }

    public void openEditFavDialog(LocationItem item)
    {
//        DialogFragment newFragment = new EditFavFragment();
//        Bundle ars = new Bundle();
//        ars.putString("item_name", item.getName());
//        ars.putString("item_address", item.getAddress());
////        Log.d(TAG, ars.toString());
//        newFragment.setArguments(ars);
//        Log.d(TAG, newFragment.getArguments().toString());
//        newFragment.show(getFragmentManager(), "Edit Favourite");

        DialogFragment newFragment = EditFavFragment.newInstance(item.getName(), item.getAddress());
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "Edit Favourite");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String edited_name, String edited_address) {
        Log.d(TAG, "CLICKED: OK");
        Log.d(TAG, edited_address + " " + edited_name);
        db.updateLocation(editableItemID, edited_name, edited_address, 0,0,0);
        favoriteListAdapter.notifyItemChanged(0);
        favoriteListAdapter.notifyDataSetChanged();
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), db.getAllFavourites());
        recyclerView.setAdapter(favoriteListAdapter);
        favoriteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "CLICKED: CANCEL");
    }
}
