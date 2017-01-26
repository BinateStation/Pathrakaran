package rkr.binatestation.pathrakkaran.fragments.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import rkr.binatestation.pathrakkaran.R;

/**
 * Dialog fragment to show simple alert dialog
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String TAG = "AlertDialogFragment";

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";

    public AlertDialogFragment() {
        // Required empty public constructor
    }

    public static AlertDialogFragment newInstance(String message) {
        Log.d(TAG, "newInstance() called with: message = [" + message + "]");
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AlertDialogFragment newInstance(String title, String message) {
        Log.d(TAG, "newInstance() called with: title = [" + title + "], message = [" + message + "]");
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        if (bundle != null) {
            if (bundle.containsKey(KEY_TITLE)) {
                alertDialogBuilder.setTitle(bundle.getString(KEY_TITLE));
            } else {
                alertDialogBuilder.setTitle(R.string.alert);
            }
            alertDialogBuilder.setMessage(bundle.getString(KEY_MESSAGE));
        }
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return alertDialogBuilder.create();
    }


}
