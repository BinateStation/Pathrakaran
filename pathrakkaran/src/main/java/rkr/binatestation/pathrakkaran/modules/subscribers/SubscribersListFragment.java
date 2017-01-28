package rkr.binatestation.pathrakkaran.modules.subscribers;


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
import rkr.binatestation.pathrakkaran.adapters.UsersAdapter;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribersListFragment extends Fragment implements SubscriberListeners.ViewListener, View.OnClickListener {

    private static final String TAG = "SubscribersListFragment";

    private ContentLoadingProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long userId = 0;
    private SubscriberListeners.PresenterListener mPresenterListener;
    private UsersAdapter mUsersAdapter;
    private AddSubscriberFragment mAddSubscriberFragment;
    private ActionBar mActionBar;

    public SubscribersListFragment() {
        // Required empty public constructor
    }


    public static SubscribersListFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        SubscribersListFragment fragment = new SubscribersListFragment();
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
        mPresenterListener = new SubscriberPresenter(this);
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
        recyclerView.setAdapter(mUsersAdapter = new UsersAdapter());

        //Setting addProduct button
        addProduct.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPresenterLive()) {
                    mPresenterListener.loadSubscriberList(getLoaderManager(), userId);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPresenterLive()) {
            mPresenterListener.loadSubscriberList(getLoaderManager(), userId);
        }
        setActionBar(
                getString(R.string.subscribers_list),
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
    public void setRecyclerView(List<UserDetailsModel> userDetailsModelList) {
        Log.d(TAG, "setRecyclerView() called with: userDetailsModelList = [" + userDetailsModelList + "]");
        if (mUsersAdapter != null) {
            mUsersAdapter.setUserDetailsModelList(userDetailsModelList);
        }
        if (mAddSubscriberFragment != null && mAddSubscriberFragment.isResumed()) {
            mAddSubscriberFragment.hideProgress();
            mAddSubscriberFragment.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FL_action_add_product:
                mAddSubscriberFragment = AddSubscriberFragment.newInstance(new AddSubscriberFragment.AddSubscriberListener() {
                    @Override
                    public void submit(String name, String mobile, String email) {
                        if (isPresenterLive()) {
                            mPresenterListener.registerSubscriber(getContext(), name, mobile, email, userId);
                        }
                    }
                });
                mAddSubscriberFragment.show(getChildFragmentManager(), mAddSubscriberFragment.getTag());
                break;
        }
    }
}
