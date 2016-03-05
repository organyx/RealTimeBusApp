package com.example.vacho.realtimebusapp;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import model.FavoriteItem;
import utils.DatabaseHelper;

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

    private static String[] names = null;
    private static String[] desrcs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_favourites_screen, container, false);

        db = new DatabaseHelper(getActivity());
//        if (!db.isEmpty())
//        {
//            for (int i = 0; i < db.getAll().getCount(); i++) {
//                names[i] = db.getAll().getString(1);
//            }
//        }

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
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateFavouriteItem(editTextName.getText().toString(), editTextDescr.getText().toString(), 0, 0, 0);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteFavouriteItem(editTextName.getText().toString());
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getAll();
            }
        });

        return v;
    }

    public static List<FavoriteItem> getData() {
        List<FavoriteItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < names.length; i++) {
            FavoriteItem favoriteItem = new FavoriteItem();
            favoriteItem.setName(names[i]);
            data.add(favoriteItem);
        }
        return data;
    }
}
