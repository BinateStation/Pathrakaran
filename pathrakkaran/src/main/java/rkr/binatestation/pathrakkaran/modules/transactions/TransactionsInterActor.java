package rkr.binatestation.pathrakkaran.modules.transactions;

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
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_TRANSACTIONS;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_SUPPLIERS_GET_LIST;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_SUPPLIERS_REGISTER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Created by RKR on 28/1/2017.
 * TransactionsInterActor.
 */

class TransactionsInterActor implements TransactionListeners.InterActorListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TransactionsInterActor";

    private TransactionListeners.PresenterListener mPresenterListener;

    TransactionsInterActor(TransactionListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void loadTransactionList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadTransactionList() called with: loaderManager = [" + loaderManager + "], userId = [" + userId + "]");
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_TRANSACTIONS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_TRANSACTIONS, bundle, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_TRANSACTIONS, bundle, this);
        }
        getTransactionsFromServer(userId);
    }

    private void getTransactionsFromServer(final long userId) {
        Log.d(TAG, "getTransactionsFromServer() called with: userId = [" + userId + "]");
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
                                DatabaseOperationService.startActionSaveUsers(context, response, new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        ArrayList<UserDetailsModel> productModelArrayList = resultData.getParcelableArrayList(KEY_SUCCESS_MESSAGE);
                                        if (isPresenterLive()) {
                                            mPresenterListener.setTransactionList(productModelArrayList);
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
    public void addTransaction(Context context, final long userId) {
        Log.d(TAG, "addTransaction() called with: context = [" + context + "], userId = [" + userId + "]");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                VolleySingleTon.getDomainUrl() + END_URL_SUPPLIERS_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        getTransactionsFromServer(userId);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_TRANSACTIONS:
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
            case CURSOR_LOADER_LOAD_TRANSACTIONS:
                if (isPresenterLive()) {
                    mPresenterListener.setTransactionList(UserDetailsModel.getAll(data));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
