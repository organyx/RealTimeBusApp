package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vacho.realtimebusapp.R;

import java.util.List;

import model.LocationItem;

/**
 * Created by Aleks on 13-Apr-16.
 * Adapter class for Location list.
 */
public class LocationListViewAdapter extends ArrayAdapter {
    Context context;
    int layoutResourceId;
    List<LocationItem> data;

    /**
     * Adapter constructor.
     * @param context Context.
     * @param resource Resource.
     * @param data Location List.
     */
    public LocationListViewAdapter(Context context, int resource, List<LocationItem> data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LocationInfoHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new LocationInfoHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.location_image_view);
            holder.txtTitle1 = (TextView) row.findViewById(R.id.location_text_view);

            row.setTag(holder);
        } else {
            holder = (LocationInfoHolder) row.getTag();
        }

        LocationItem b = data.get(position);
        holder.imgIcon.setImageResource(R.drawable.ic_place_black_36dp);
        holder.txtTitle1.setText(b.getName());

        return row;
    }

    static class LocationInfoHolder {
        ImageView imgIcon;
        TextView txtTitle1;
    }
}
