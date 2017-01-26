package rkr.binatestation.pathrakkaran.modules.suppliers;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.adapters.UsersAdapter;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Fragment to load agent product list
 */
public class SuppliersListFragment extends Fragment implements SuppliersListeners.ViewListener, View.OnClickListener {


    private static final String TAG = "SuppliersListFragment";
    private ContentLoadingProgressBar mProgressBar;
    private long userId = 0;
    private SuppliersListeners.PresenterListener mPresenterListener;
    private UsersAdapter mUsersAdapter;
    private AddSupplierFragment mAddSupplierFragment;

    public SuppliersListFragment() {
        // Required empty public constructor
    }

    public static SuppliersListFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        SuppliersListFragment fragment = new SuppliersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterListener = new SuppliersPresenter(this);
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

        //Setting Recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUsersAdapter = new UsersAdapter());

        //Setting addProduct button
        addProduct.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPresenterLive()) {
            mPresenterListener.loadSuppliersList(getLoaderManager(), userId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FL_action_add_product:
                mAddSupplierFragment = AddSupplierFragment.newInstance(new AddSupplierFragment.AddSupplierListener() {
                    @Override
                    public void submit(String name, String mobile, String email) {
                        if (isPresenterLive()) {
                            mPresenterListener.registerSupplier(getContext(), name, mobile, email, userId);
                        }
                    }
                });
                mAddSupplierFragment.show(getChildFragmentManager(), mAddSupplierFragment.getTag());
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
    }

    @Override
    public void setRecyclerView(List<UserDetailsModel> userDetailsModelList) {
        Log.d(TAG, "setRecyclerView() called with: userDetailsModelList = [" + userDetailsModelList + "]");
        if (mUsersAdapter != null) {
            mUsersAdapter.setUserDetailsModelList(userDetailsModelList);
        }
        if (mAddSupplierFragment != null && mAddSupplierFragment.isResumed()) {
            mAddSupplierFragment.hideProgress();
            mAddSupplierFragment.dismiss();
        }
    }

}
