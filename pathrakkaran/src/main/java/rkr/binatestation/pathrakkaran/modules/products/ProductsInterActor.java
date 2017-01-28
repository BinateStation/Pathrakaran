package rkr.binatestation.pathrakkaran.modules.products;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakkaran.database.DatabaseOperationService;
import rkr.binatestation.pathrakkaran.models.AgentProductModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static rkr.binatestation.pathrakkaran.database.DatabaseOperationService.KEY_SUCCESS_MESSAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_AGENT_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_PRODUCTS_MY_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Created by RKR on 8/1/2017.
 * ProductsInterActor.
 */

class ProductsInterActor implements ProductsListeners.InterActorListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ProductsInterActor";
    private ProductsListeners.PresenterListener mPresenterListener;

    ProductsInterActor(ProductsListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void loadProductList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadSuppliersList() called with: loaderManager = [" + loaderManager + "]");
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, bundle, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, bundle, this);
        }
        getProductsFromServer(userId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_AGENT_PRODUCTS:
                if (isPresenterLive()) {
                    Context context = mPresenterListener.getContext();
                    if (context != null) {
                        long userId = args.getLong(KEY_USER_ID, 0);
                        return AgentProductModel.getAgentProductModelList(context, userId);
                    }
                }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case CURSOR_LOADER_LOAD_AGENT_PRODUCTS:
                if (isPresenterLive()) {
                    mPresenterListener.setProductList(AgentProductModel.getAgentProductModelList(data));
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void getProductsFromServer(final long userId) {
        Log.d(TAG, "getProductsFromServer() called");
        if (isPresenterLive()) {
            final Context context = mPresenterListener.getContext();
            if (context != null) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        VolleySingleTon.getDomainUrl() + END_URL_PRODUCTS_MY_PRODUCTS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                                DatabaseOperationService.startActionSaveProductAgent(context, response, new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        ArrayList<AgentProductModel> productModelArrayList = resultData.getParcelableArrayList(KEY_SUCCESS_MESSAGE);
                                        if (isPresenterLive()) {
                                            mPresenterListener.setProductList(productModelArrayList);
                                        }
                                    }
                                });
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "onErrorResponse: ", error);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(KEY_AGENT_ID, "" + userId);

                        Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                        return params;
                    }
                };
                VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
            }
        }
    }
}
