package rkr.binatestation.pathrakkaran.modules.transactions;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.adapters.TransactionAdapter;
import rkr.binatestation.pathrakkaran.models.TransactionModel;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Fragment to list out the list of transactions
 */
public class TransactionListFragment extends Fragment implements TransactionListeners.ViewListener, View.OnClickListener {

    private static final String TAG = "TransactionListFragment";

    private ContentLoadingProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long userId = 0;
    private TransactionListeners.PresenterListener mPresenterListener;
    private TransactionAdapter mTransactionAdapter;
    private AddTransactionFragment mAddTransactionFragment;
    private ActionBar mActionBar;

    public TransactionListFragment() {
        // Required empty public constructor
    }

    public static TransactionListFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        TransactionListFragment fragment = new TransactionListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            mActionBar = activity.getSupportActionBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPresenterListener = new TransactionPresenter(this);
        userId = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getLong(KEY_USER_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FL_recycler_view);
        FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.FL_action_add_product);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.FL_progress_bar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.FL_swipe_refresh);

        //Setting Recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mTransactionAdapter = new TransactionAdapter());

        //Setting addProduct button
        addProduct.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPresenterLive()) {
                    mPresenterListener.loadTransactionList(getLoaderManager(), userId);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPresenterLive()) {
            mPresenterListener.loadTransactionList(getLoaderManager(), userId);
        }
        setActionBar(
                getString(R.string.transactions),
                getString(R.string.app_name)
        );
    }

    private void setActionBar(String title, String subtitle) {
        if (mActionBar != null) {
            mActionBar.setTitle(title);
            mActionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FL_action_add_product:
                mAddTransactionFragment = AddTransactionFragment.newInstance(new AddTransactionFragment.AddTransactionListener() {
                    @Override
                    public void addTransaction() {
                        if (isPresenterLive()) {
                            mPresenterListener.addTransaction(getContext(), userId);
                        }
                    }
                });
                mAddTransactionFragment.show(getChildFragmentManager(), mAddTransactionFragment.getTag());
                break;
        }
    }

    @Override
    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.show();
        }
    }

    @Override
    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void setRecyclerView(List<TransactionModel> transactionModelList) {
        Log.d(TAG, "setRecyclerView() called with: transactionModelList = [" + transactionModelList + "]");
        if (mTransactionAdapter != null) {
            mTransactionAdapter.setTransactionModelList(transactionModelList);
        }
        if (mAddTransactionFragment != null && mAddTransactionFragment.isResumed()) {
            mAddTransactionFragment.hideProgress();
            mAddTransactionFragment.dismiss();
        }
    }
}
