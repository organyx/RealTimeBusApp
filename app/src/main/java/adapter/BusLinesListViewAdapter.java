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

import model.BusLineItem;

/**
 * Created by Vacho on 3/21/2016.
 */
public class BusLinesListViewAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    List<BusLineItem> data;

    public BusLinesListViewAdapter(Context context, int resource, List<BusLineItem> data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BusLineInfoHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BusLineInfoHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.bus_lines_image_view);
            holder.txtTitle1 = (TextView) row.findViewById(R.id.bus_lines_text_view);

            row.setTag(holder);
        } else {
            holder = (BusLineInfoHolder) row.getTag();
        }

        BusLineItem b = data.get(position);
        holder.imgIcon.setImageResource(R.drawable.ic_flag_24dp);
        holder.txtTitle1.setText(b.getBusLineName());

        return row;
    }

    static class BusLineInfoHolder {
        ImageView imgIcon;
        TextView txtTitle1;
    }
}
