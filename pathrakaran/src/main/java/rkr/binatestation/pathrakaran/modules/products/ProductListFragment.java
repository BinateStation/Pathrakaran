package rkr.binatestation.pathrakaran.modules.products;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.adapters.ProductAdapter;
import rkr.binatestation.pathrakaran.models.AgentProductModel;

import static rkr.binatestation.pathrakaran.database.PathrakaranContract.AgentProductListTable.CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER;
import static rkr.binatestation.pathrakaran.utils.Constants.CURSOR_LOADER_LOAD_AGENT_PRODUCTS;

/**
 * Fragment to show the list of products
 */
public class ProductListFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ProductListFragment";

    private ContentLoadingProgressBar mProgressBar;
    private ProductAdapter mProductAdapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
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
        loadProductList();
    }

    private void loadProductList() {
        Log.d(TAG, "loadProductList() called");
        if (mProgressBar != null) {
            mProgressBar.show();
        }
        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, null, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, null, this);
        }
    }

    private void setRecyclerView(List<AgentProductModel> productModelList) {
        Log.d(TAG, "setRecyclerView() called with: productModelList = [" + productModelList + "]");
        if (mProductAdapter != null) {
            mProductAdapter.setProductModelList(productModelList);
        }
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FPL_action_add_product:
                break;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_AGENT_PRODUCTS:
                return new CursorLoader(
                        getContext(),
                        CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                        null,
                        null,
                        null,
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case CURSOR_LOADER_LOAD_AGENT_PRODUCTS:
                setRecyclerView(AgentProductModel.getAgentProductModelList(data));
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
    }
}
