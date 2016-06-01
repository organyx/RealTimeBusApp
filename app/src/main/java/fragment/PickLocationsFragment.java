package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacho.realtimebusapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import adapter.PlaceArrayAdapter;

/**
 * Created by Aleks on 22-May-16.
 * Popup for picking traveling options.
 */
public class PickLocationsFragment extends DialogFragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "PickLocFragment";

    private AutoCompleteTextView et_from;
    private AutoCompleteTextView et_to;
    private TextView tv_notification;

    private List<Place> points;

    private GoogleApiClient googleApiClient;
    private PlaceArrayAdapter placeArrayAdapter;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    public static PickLocationsFragment newInstance() {
        return new PickLocationsFragment();
    }


    public interface LocationPickedListener {
        void onDialogLocPositiveClick(DialogFragment dialog, List<Place> places);

        void onDialogLocNegativeClick(DialogFragment dialog);
    }

    LocationPickedListener locationPickedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            locationPickedListener = (LocationPickedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        final View thisView = inflater.inflate(R.layout.pop_up_pick_loc, null);

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

        points = new ArrayList<>();

        tv_notification = (TextView) thisView.findViewById(R.id.tv_notification);
        tv_notification.setVisibility(View.INVISIBLE);

        et_from = (AutoCompleteTextView) thisView.findViewById(R.id.pop_up_edit_loc_from);
        et_from.setThreshold(1);
        et_to = (AutoCompleteTextView) thisView.findViewById(R.id.pop_up_edit_loc_to);
        et_to.setThreshold(1);
        if (et_to == null) {
            Log.d(TAG, "NOT DONE");
        }
        Bundle myArgs = getArguments();
        if (myArgs != null) {
            if (myArgs.containsKey("item_name")) {
                Log.d(TAG, myArgs.toString());
                et_from.setText(myArgs.getString("item_name"));
                et_to.setText(myArgs.getString("item_address"));
            }
        }

        et_from.setOnItemClickListener(mAutocompleteClickListener);
        et_to.setOnItemClickListener(mAutocompleteClickListener);

        placeArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);

        et_from.setAdapter(placeArrayAdapter);
        et_to.setAdapter(placeArrayAdapter);

        final LocationPickedListener activity = (LocationPickedListener) getActivity();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(thisView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        activity.onDialogPositiveClick(EditFavFragment.this, et_name.getText().toString(), et_address.getText().toString());
                        Log.d(TAG, "CLICKED: OK");

                        if (points.size() < 2) {
                            tv_notification.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else {
                            ((LocationPickedListener) getTargetFragment()).onDialogLocPositiveClick(PickLocationsFragment.this, points);

                            if (points.size() > 0)
                                Log.d(TAG, "Items in the list " + points.size());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        activity.onDialogNegativeClick(EditFavFragment.this);
                        Log.d(TAG, "CLICKED: CANCEL");
                        ((LocationPickedListener) getTargetFragment()).onDialogLocNegativeClick(PickLocationsFragment.this);
                        PickLocationsFragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle(R.string.where_to_travel);
        return builder.create();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

//            tv_fav_item_name.setText(Html.fromHtml(place.getName() + ""));
            points.add(place);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placeArrayAdapter.setGoogleApiClient(googleApiClient);
        Log.i(TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        placeArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
}
