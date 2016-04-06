package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vacho.realtimebusapp.R;

import java.util.Collections;
import java.util.List;

import model.LocationItem;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.MyFavViewHolder> {

    List<LocationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public FavoriteListAdapter(Context context, List<LocationItem> data)
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
        LocationItem current = data.get(position);
        holder.name.setText(current.getName());
        holder.descr.setText(current.getAddress());
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

    public void removeItem(LocationItem item)
    {
        int currentPos = data.indexOf(item);
        data.remove(currentPos);
        notifyItemRemoved(currentPos);
    }

    public void addItem(int position, LocationItem item)
    {
        data.add(position, item);
        notifyItemInserted(position);
    }
}
