package fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

    private static final String TAG = "AboutScreenFragment";
    private FloatingActionButton fab;

    public AboutScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_screen, container, false);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();
        return view;
    }
}
