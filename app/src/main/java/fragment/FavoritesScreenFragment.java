package fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.vacho.realtimebusapp.R;

import java.util.List;

import adapter.FavoriteListAdapter;
import model.FavoriteItem;
import utils.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesScreenFragment extends Fragment {

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

        final List<FavoriteItem> list = db.getAllFavourites();

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_fav_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FavoriteListAdapter(getActivity(), list);
//        adapter.setHasStableIds(true);
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
//                db.addNewFavourite(editTextName.getText().toString(), editTextDescr.getText().toString(), 55.86544499999999, 9.843203000000017, 0);
//                adapter.notifyItemInserted(list.size());
//                adapter.notifyDataSetChanged();

//                HomeScreenFragment fr = new HomeScreenFragment();
//                Bundle args = new Bundle();
////                args.putInt(fr.pos, position);
//                fr.setArguments(args);
//
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                transaction.replace(R.id.fragment_container, fr);
//                transaction.addToBackStack(null);
//                transaction.commit();
                IntermediateMapFragment mapFragment = new IntermediateMapFragment();
                Bundle args = new Bundle();
                mapFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                adapter = new FavoriteListAdapter(getActivity(), db.getAllFavourites());
                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }

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
