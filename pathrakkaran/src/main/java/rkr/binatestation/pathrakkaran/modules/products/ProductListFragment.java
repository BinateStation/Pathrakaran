package rkr.binatestation.pathrakkaran.modules.products;


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
import rkr.binatestation.pathrakkaran.adapters.ProductAdapter;
import rkr.binatestation.pathrakkaran.models.AgentProductModel;

/**
 * Fragment to show the list of products
 */
public class ProductListFragment extends Fragment implements View.OnClickListener,
        ProductsListeners.ViewListener {

    private static final String TAG = "ProductListFragment";

    private ContentLoadingProgressBar mProgressBar;
    private ProductAdapter mProductAdapter;

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
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenterListener = new ProductsPresenter(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.FPL_recycler_view);
        FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.FPL_action_add_product);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.FPL_progress_bar);

        //Setting Recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mProductAdapter = new ProductAdapter());

        //Setting addProduct button
        addProduct.setOnClickListener(this);
        if (isPresenterLive()) {
            mPresenterListener.loadProductList(getLoaderManager());
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
            case R.id.FPL_action_add_product:
                AddProductFragment addProductFragment = AddProductFragment.newInstance();
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
    }
}
