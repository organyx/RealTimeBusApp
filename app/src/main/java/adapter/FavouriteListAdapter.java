package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vacho.realtimebusapp.R;

import java.util.Collections;
import java.util.List;

import model.FavoriteItem;
import model.NavigationDrawerItem;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyFavViewHolder> {

    List<FavoriteItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public FavouriteListAdapter(Context context, List<FavoriteItem> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyFavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.content_favourite_list_item, parent, false);
        MyFavViewHolder holder = new MyFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyFavViewHolder holder, int position) {
        FavoriteItem current = data.get(position);
        holder.name.setText(current.getName());
        holder.descr.setText(current.getDescr());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyFavViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView descr;

        public MyFavViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            descr = (TextView) itemView.findViewById(R.id.tv_descr);
        }
    }
}
