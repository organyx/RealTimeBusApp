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

import java.util.ArrayList;
import java.util.List;

import model.BusStationInfo;

/**
 * Created by Vacho on 3/11/2016.
 */
public class CustomListViewAdapter extends ArrayAdapter {

   Context context;
    int layoutResourceId;
    BusStationInfo data[]= null;

    public CustomListViewAdapter(Context context, int layoutResourceId,  BusStationInfo[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.image_da);
            holder.txtTitle1 = (TextView)row.findViewById(R.id.sss);
            holder.txtTitle2 = (TextView)row.findViewById(R.id.nnn);


            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }

        BusStationInfo b = data[position];
        holder.imgIcon.setImageResource(b.getIcon());
        holder.txtTitle1.setText(b.getBusStationName());
        holder.txtTitle2.setText(b.getBusLines());

        return row;
    }

    static class WeatherHolder
    {
        ImageView imgIcon;
        TextView txtTitle1;
        TextView txtTitle2;
    }

}
