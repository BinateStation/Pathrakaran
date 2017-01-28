package rkr.binatestation.pathrakkaran.modules.products;


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
import rkr.binatestation.pathrakkaran.adapters.ProductAdapter;
import rkr.binatestation.pathrakkaran.models.AgentProductModel;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Fragment to show the list of products
 */
public class ProductListFragment extends Fragment implements View.OnClickListener,
        ProductsListeners.ViewListener {

    private static final String TAG = "ProductListFragment";

    private ContentLoadingProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProductAdapter mProductAdapter;
    private long userId = 0;
    private ActionBar mActionBar;

    private ProductsListeners.PresenterListener mPresenterListener;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public static ProductListFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        ProductListFragment fragment = new ProductListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
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
        mPresenterListener = new ProductsPresenter(this);
        userId = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getLong(KEY_USER_ID, 0);
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
        recyclerView.setAdapter(mProductAdapter = new ProductAdapter());

        //Setting addProduct button
        addProduct.setOnClickListener(this);
        if (isPresenterLive()) {
            mPresenterListener.loadProductList(getLoaderManager(), userId);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isPresenterLive()) {
                    mPresenterListener.loadProductList(getLoaderManager(), userId);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(
                getString(R.string.products_list),
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
    public void setRecyclerView(List<AgentProductModel> productModelList) {
        Log.d(TAG, "setRecyclerView() called with: productModelList = [" + productModelList + "]");
        if (mProductAdapter != null) {
            mProductAdapter.setProductModelList(productModelList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FL_action_add_product:
                AddProductFragment addProductFragment = AddProductFragment.newInstance(userId, new AddProductFragment.AddProductListener() {
                    @Override
                    public void onFinishListener() {
                        if (isPresenterLive()) {
                            mPresenterListener.loadProductList(getLoaderManager(), userId);
                        }
                    }
                });
                addProductFragment.show(getChildFragmentManager(), addProductFragment.getTag());
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
}
