package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.vacho.realtimebusapp.R;

/**
 * Created by Aleks on 14-Apr-16.
 * Popup for editing favourite location item.
 */
public class EditFavFragment extends DialogFragment {

    private static final String TAG = "EditFavFrag";

    EditText et_name;
    EditText et_address;

    public static EditFavFragment newInstance(String name, String address) {
        EditFavFragment frag = new EditFavFragment();
        Bundle args = new Bundle();
        args.putString("item_name", name);
        args.putString("item_address", address);
        frag.setArguments(args);
        return frag;
    }


    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String edited_name, String edited_address);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener noticeDialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            noticeDialogListener = (NoticeDialogListener) activity;
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

        final View thisView = inflater.inflate(R.layout.pop_up_edit_favourite, null);
        et_name = (EditText) thisView.findViewById(R.id.pop_up_edit_fav_name);
        et_address = (EditText) thisView.findViewById(R.id.pop_up_edit_fav_address);
        if (et_address == null) {
            Log.d(TAG, "NOT DONE");
        }
        Bundle myArgs = getArguments();
        if (myArgs != null) {
            if (myArgs.containsKey("item_name")) {
                Log.d(TAG, myArgs.toString());
                et_name.setText(myArgs.getString("item_name"));
                et_address.setText(myArgs.getString("item_address"));
            }
        }
        final NoticeDialogListener activity = (NoticeDialogListener) getActivity();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(thisView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        activity.onDialogPositiveClick(EditFavFragment.this, et_name.getText().toString(), et_address.getText().toString());
                        Log.d(TAG, "CLICKED: OK");
                        ((NoticeDialogListener) getTargetFragment()).onDialogPositiveClick(EditFavFragment.this, et_name.getText().toString(), et_address.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        activity.onDialogNegativeClick(EditFavFragment.this);
                        Log.d(TAG, "CLICKED: CANCEL");
                        ((NoticeDialogListener) getTargetFragment()).onDialogNegativeClick(EditFavFragment.this);
                        EditFavFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
