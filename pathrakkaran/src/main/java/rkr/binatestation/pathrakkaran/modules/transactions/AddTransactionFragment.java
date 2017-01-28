package rkr.binatestation.pathrakkaran.modules.transactions;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.pathrakkaran.R;

/**
 * Dialog fragment for a new transaction
 */
public class AddTransactionFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "AddTransactionFragment";

    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private AddTransactionListener mAddTransactionListener;

    public AddTransactionFragment() {
        // Required empty public constructor
    }


    public static AddTransactionFragment newInstance(AddTransactionListener addTransactionListener) {
        Log.d(TAG, "newInstance() called with: addTransactionListener = [" + addTransactionListener + "]");
        Bundle args = new Bundle();

        AddTransactionFragment fragment = new AddTransactionFragment();
        fragment.setArguments(args);
        fragment.mAddTransactionListener = addTransactionListener;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    public void showProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.show();
        }
    }

    public void hideProgress() {
        if (mContentLoadingProgressBar != null) {
            mContentLoadingProgressBar.hide();
        }
    }

    @Override
    public void onClick(View v) {
        if (mAddTransactionListener != null) {
            mAddTransactionListener.addTransaction();
        }
    }

    interface AddTransactionListener {
        void addTransaction();
    }
}
