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

import model.BusStationInfo;

/**
 * Created by Vacho on 3/21/2016.
 */
public class BusListViewAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    BusStationInfo data[] = null;

    public BusListViewAdapter(Context context, int resource,  BusStationInfo data[]) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BusLineInfoHolder holder = null;

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

        BusStationInfo b = data[position];
        holder.imgIcon.setImageResource(b.getIcon());
        holder.txtTitle1.setText(b.getBusLines());

        return row;
    }

    static class BusLineInfoHolder {
        ImageView imgIcon;
        TextView txtTitle1;
    }
}
