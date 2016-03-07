package com.example.vacho.realtimebusapp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import adapter.FavouriteListAdapter;
import utils.DatabaseHelper;
import model.FavouriteItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesScreenFragment extends Fragment {

    Button btnAdd;
    Button btnUpdate;
    Button btnDelete;
    Button btnRefresh;
    DatabaseHelper db;
    EditText editTextName;
    EditText editTextDescr;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_favourites_screen, container, false);

        db = new DatabaseHelper(getActivity());

        final List<FavouriteItem> list = db.getAllFavourites();

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_fav_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FavouriteListAdapter(getActivity(), list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
//            Toast.makeText(getActivity(), n, Toast.LENGTH_LONG);
//        Log.d("FavScreenFragment", "Something: " + list.get(0).getName());

        btnAdd = (Button) v.findViewById(R.id.btn_add_fav);
        btnUpdate = (Button) v.findViewById(R.id.btn_update_fav);
        btnDelete = (Button) v.findViewById(R.id.btn_del_fav);
        btnRefresh = (Button) v.findViewById(R.id.btn_refresh_fav);
        editTextName = (EditText) v.findViewById(R.id.et_fav_name);
        editTextDescr = (EditText) v.findViewById(R.id.et_fav_address);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addNewFavourite(editTextName.getText().toString(), editTextDescr.getText().toString(), 0, 0, 0);
                adapter.notifyItemInserted(list.size());
//                adapter.notifyDataSetChanged();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateFavouriteItem(editTextName.getText().toString(), editTextDescr.getText().toString(), 0, 0, 0);
                adapter.notifyDataSetChanged();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteFavouriteItem(editTextName.getText().toString());
                adapter.notifyItemRemoved(list.size() - 1);
//                adapter.notifyDataSetChanged();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new FavouriteListAdapter(getActivity(), db.getAllFavourites());
                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }
//
//    public static List<FavoriteItem> getData() {
//        List<FavoriteItem> data = new ArrayList<>();
//
//        // preparing navigation drawer items
//        for (int i = 0; i < names.length; i++) {
//            FavoriteItem favoriteItem = new FavoriteItem();
//            favoriteItem.setName(names[i]);
//            data.add(favoriteItem);
//        }
//        return data;
//    }

    public void refreshActivity()
    {
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
