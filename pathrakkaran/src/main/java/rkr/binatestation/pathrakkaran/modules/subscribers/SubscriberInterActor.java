package rkr.binatestation.pathrakkaran.modules.subscribers;

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
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static android.content.Context.MODE_PRIVATE;
import static rkr.binatestation.pathrakkaran.database.DatabaseOperationService.KEY_SUCCESS_MESSAGE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_USER_TYPE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUBSCRIBER;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_SUBSCRIBERS;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_USER_GET_USERS_LIST;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USERS_LAST_UPDATED_DATE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Created by RKR on 28/1/2017.
 * SubscriberInterActor.
 */

class SubscriberInterActor implements SubscriberListeners.InterActorListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SubscriberInterActor";

    private SubscriberListeners.PresenterListener mPresenterListener;

    SubscriberInterActor(SubscriberListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void loadSubscriberList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadSubscriberList() called with: loaderManager = [" + loaderManager + "], userId = [" + userId + "]");
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_SUBSCRIBERS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_SUBSCRIBERS, bundle, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_SUBSCRIBERS, bundle, this);
        }
        getSubscribersFromServer(userId);
    }

    private void getSubscribersFromServer(final long userId) {
        Log.d(TAG, "getSubscribersFromServer() called with: userId = [" + userId + "]");
        if (isPresenterLive()) {
            final Context context = mPresenterListener.getContext();
            if (context != null) {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        VolleySingleTon.getDomainUrl() + END_URL_USER_GET_USERS_LIST,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                                DatabaseOperationService.startActionSaveUsers(context, USER_TYPE_SUBSCRIBER, response, new ResultReceiver(new Handler()) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        ArrayList<UserDetailsModel> userDetailsModelArrayList = resultData.getParcelableArrayList(KEY_SUCCESS_MESSAGE);
                                        if (isPresenterLive() && userDetailsModelArrayList != null && userDetailsModelArrayList.size() > 0) {
                                            mPresenterListener.setSubscriberList(userDetailsModelArrayList);
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
                        long lastUpdatedDate = GeneralUtils.getUnixTimeStamp(context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE)
                                .getLong(KEY_USERS_LAST_UPDATED_DATE, 0));
                        Map<String, String> params = new HashMap<>();
                        params.put(KEY_AGENT_ID, "" + userId);
                        params.put(KEY_DATE, "" + lastUpdatedDate);

                        Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                        return params;
                    }
                };
                VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
            }
        }

    }

    @Override
    public void register(Context context, final String name, final String mobile, final String email, final int userTypeValue, final long userId) {
        Log.d(TAG, "register() called with: context = [" + context + "], name = [" + name + "], mobile = [" + mobile + "], email = [" + email + "], userTypeValue = [" + userTypeValue + "], userId = [" + userId + "]");
        final UserDetailsModel userDetailsModel = UserDetailsModel.getDefault();
        userDetailsModel.setName(name);
        userDetailsModel.setMobile(mobile);
        userDetailsModel.setEmail(email);
        userDetailsModel.setUserType(userTypeValue);
        DatabaseOperationService.startActionAddUsers(context, userId, userDetailsModel, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (isPresenterLive()) {
                    mPresenterListener.addToSubscriberList(userDetailsModel);
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        switch (id) {
            case CURSOR_LOADER_LOAD_SUBSCRIBERS:
                if (isPresenterLive()) {
                    Context context = mPresenterListener.getContext();
                    if (context != null) {
                        return new CursorLoader(
                                context,
                                CONTENT_URI,
                                null,
                                COLUMN_USER_TYPE + " = ? ",
                                new String[]{"" + USER_TYPE_SUBSCRIBER},
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
            case CURSOR_LOADER_LOAD_SUBSCRIBERS:
                if (isPresenterLive()) {
                    mPresenterListener.setSubscriberList(UserDetailsModel.getAll(data));
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
