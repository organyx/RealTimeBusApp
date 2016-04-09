package fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutScreenFragment extends Fragment {


    public AboutScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_screen, container, false);
        ImageView v = (ImageView) view.findViewById(R.id.omg);
        if(v != null){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"DODO", Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }

}
