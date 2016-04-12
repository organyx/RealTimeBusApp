package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;

import java.util.Collections;
import java.util.List;

import model.LocationItem;
import fragment.FavoritesScreenFragment;
import model.FavoriteItem;
import utils.DatabaseHelper;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.MyFavViewHolder> {

    List<LocationItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;


    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onClick(View itemView);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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
    public void onBindViewHolder(final MyFavViewHolder holder, final int position) {
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
        ImageView iv;
        RelativeLayout r;

        public MyFavViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            descr = (TextView) itemView.findViewById(R.id.tv_descr);
            iv = (ImageView) itemView.findViewById(R.id.favourite_image_view);
            r = (RelativeLayout)itemView.findViewById(R.id.aaaa);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView,getPosition());
                    }
                }
            });

            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(itemView);
                    }
                }
            });
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
