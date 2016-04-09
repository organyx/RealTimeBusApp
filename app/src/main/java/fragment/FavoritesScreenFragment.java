package fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;

import java.util.List;

import adapter.FavoriteListAdapter;
import model.FavoriteItem;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesScreenFragment extends Fragment {



    DatabaseHelper db;
    private FloatingActionButton addButton;
    private PopupMenu popupMenu;
    private RecyclerView recyclerView;
    private FavoriteListAdapter favoriteListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String temp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_favourites_screen, container, false);
        db = new DatabaseHelper(getActivity());
        final List<FavoriteItem> list = db.getAllFavourites();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_fav_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        favoriteListAdapter = new FavoriteListAdapter(getActivity(), list);
        recyclerView.setAdapter(favoriteListAdapter);

        favoriteListAdapter.setOnItemClickListener(new FavoriteListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View itemView, int position) {
                FavoriteItem item = db.getAllFavourites().get(position);
                temp = item.getName();
                popupMenu = new PopupMenu(getActivity(), itemView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_favourites, popupMenu.getMenu());

                //registering popup with OnMenuItemClickListener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Edit")) {
                            Toast.makeText(
                                    itemView.getContext(),
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {

                            db.deleteFavouriteItem(temp.toString());
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

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                addButton.hide();
            }
        });

        return rootView;
    }
}
