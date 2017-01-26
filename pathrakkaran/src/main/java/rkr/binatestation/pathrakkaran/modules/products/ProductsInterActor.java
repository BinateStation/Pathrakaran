package rkr.binatestation.pathrakkaran.modules.products;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_AGENT_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_PRODUCT_LIST;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_POST_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.PRODUCTS_MY_PRODUCTS_JSON;

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
    public void loadProductList(LoaderManager loaderManager) {
        Log.d(TAG, "loadProductList() called with: loaderManager = [" + loaderManager + "]");
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, null, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_AGENT_PRODUCTS, null, this);
        }
        getProductsFromServer();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_AGENT_PRODUCTS:
                if (isPresenterLive()) {
                    Context context = mPresenterListener.getContext();
                    if (context != null) {
                        return new CursorLoader(
                                context,
                                CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                                null,
                                null,
                                null,
                                null
                        );
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

    private void getProductsFromServer() {
        Log.d(TAG, "getProductsFromServer() called");
        if (isPresenterLive()) {
            final Context context = mPresenterListener.getContext();
            if (context != null) {
                final String userId = "" + context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getLong(KEY_SP_USER_ID, 0);
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        VolleySingleTon.getDomainUrl() + PRODUCTS_MY_PRODUCTS_JSON,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                                DatabaseOperationService.startActionSaveProductAgent(context, response, new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        ArrayList<AgentProductModel> productModelArrayList = resultData.getParcelableArrayList(KEY_AGENT_PRODUCT_LIST);
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
                        params.put(KEY_POST_AGENT_ID, userId);

                        Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                        return params;
                    }
                };
                VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
            }
        }
    }
}
