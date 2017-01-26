package rkr.binatestation.pathrakkaran.modules.suppliers;

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
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static rkr.binatestation.pathrakkaran.database.DatabaseOperationService.KEY_SUCCESS_MESSAGE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_USER_TYPE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUPPLIER;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_AGENT_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_SUPPLIERS;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_SUPPLIERS_GET_LIST;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_SUPPLIERS_REGISTER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_EMAIL;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LOGIN_TYPE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MOBILE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_TYPE;

/**
 * Created by RKR on 26/1/2017.
 * SuppliersInterActor.
 */

class SuppliersInterActor implements SuppliersListeners.InterActorListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "SuppliersInterActor";
    private SuppliersListeners.PresenterListener mPresenterListener;

    SuppliersInterActor(SuppliersListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void loadSuppliersList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadSuppliersList() called with: loaderManager = [" + loaderManager + "]");
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_SUPPLIERS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_SUPPLIERS, bundle, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_SUPPLIERS, bundle, this);
        }
        getSuppliersFromServer(userId);
    }

    @Override
    public void register(Context context, final String name, final String mobile, final String email, final int userTypeValue, final long userId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                VolleySingleTon.getDomainUrl() + END_URL_SUPPLIERS_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        getSuppliersFromServer(userId);
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
                params.put(KEY_NAME, name);
                params.put(KEY_MOBILE, mobile);
                params.put(KEY_EMAIL, email);
                params.put(KEY_USER_TYPE, "" + userTypeValue);
                params.put(KEY_LOGIN_TYPE, "N");

                Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                return params;
            }
        };
        VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);

    }

    private void getSuppliersFromServer(final long userId) {
        Log.d(TAG, "getSuppliersFromServer() called with: userId = [" + userId + "]");
        if (isPresenterLive()) {
            final Context context = mPresenterListener.getContext();
            if (context != null) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        VolleySingleTon.getDomainUrl() + END_URL_SUPPLIERS_GET_LIST,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                                DatabaseOperationService.startActionSaveSuppliers(context, response, new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        ArrayList<UserDetailsModel> productModelArrayList = resultData.getParcelableArrayList(KEY_SUCCESS_MESSAGE);
                                        if (isPresenterLive()) {
                                            mPresenterListener.setSuppliersList(productModelArrayList);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_SUPPLIERS:
                if (isPresenterLive()) {
                    Context context = mPresenterListener.getContext();
                    if (context != null) {
                        return new CursorLoader(
                                context,
                                CONTENT_URI,
                                null,
                                COLUMN_USER_TYPE + " = ? ",
                                new String[]{"" + USER_TYPE_SUPPLIER},
                                COLUMN_NAME
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
                    mPresenterListener.setSuppliersList(UserDetailsModel.getAll(data));
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
