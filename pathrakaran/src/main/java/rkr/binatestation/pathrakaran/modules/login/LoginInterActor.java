package rkr.binatestation.pathrakaran.modules.login;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;

import static android.content.Context.MODE_PRIVATE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_DATA;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MESSAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MOBILE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_STATUS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LOGIN_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_PASSWORD;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_USER;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_IS_LOGGED_IN;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_PHONE;
import static rkr.binatestation.pathrakaran.utils.Constants.REQUEST_READ_CONTACTS;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_LOGIN;

/**
 * Created by RKR on 20/11/2016.
 * LoginInterActor.
 */

class LoginInterActor implements LoginListeners.InterActorListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "LoginInterActor";

    private LoginListeners.PresenterListener presenterListener;

    LoginInterActor(LoginListeners.PresenterListener presenterListener) {
        this.presenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return presenterListener != null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (isPresenterLive()) {
            Context context = presenterListener.getContext();
            if (context != null) {
                return new CursorLoader(context,
                        // Retrieve data rows for the device user's 'profile' contact.
                        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                                ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                        // Select only email addresses.
                        ContactsContract.Contacts.Data.MIMETYPE +
                                " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                        .CONTENT_ITEM_TYPE},

                        // Show primary email addresses first. Note that there won't be
                        // a primary email address if the user hasn't specified one.
                        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        List<String> emails = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            emails.add(data.getString(ProfileQuery.ADDRESS));
            data.moveToNext();
        }

        if (isPresenterLive()) {
            presenterListener.addEmailsToAutoComplete(emails);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void populateAutoComplete(LoaderManager loaderManager) {
        loaderManager.initLoader(REQUEST_READ_CONTACTS, null, this);
    }

    /**
     * Method which calls the network for activity_login with following post parameters
     *
     * @param username  the username for activity_login
     * @param password  the password for activity_login
     * @param loginType the type of activity_login like N- Normal , F- facebook, G - google etc...
     */
    @Override
    public void login(final String username, final String password, final String loginType) {
        Log.d(TAG, "activity_login() called with: username = [" + username + "], password = [" +
                password + "], loginType = [" + loginType + "]");
        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + USER_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                if (isPresenterLive()) {
                    Context context = presenterListener.getContext();
                    if (context != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString(KEY_JSON_MESSAGE);
                            if (jsonObject.has(KEY_JSON_STATUS) && 200 == jsonObject.optInt(KEY_JSON_STATUS)) {
                                if (jsonObject.has(KEY_JSON_DATA)) {
                                    JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_JSON_DATA);
                                    if (dataJsonObject != null) {
                                        context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit()
                                                .putString(KEY_SP_USER_ID, dataJsonObject.optString(KEY_JSON_USER_ID))
                                                .putString(KEY_SP_USER_NAME, dataJsonObject.optString(KEY_JSON_NAME))
                                                .putString(KEY_SP_USER_PHONE, dataJsonObject.optString(KEY_JSON_MOBILE))
                                                .putBoolean(KEY_SP_IS_LOGGED_IN, true).apply();
                                        presenterListener.onSuccessfulLogin(message != null ? message : context.getString(R.string.successfully_logged_in));
                                    } else {
                                        presenterListener.onErrorLogin(message != null ? message : context.getString(R.string.some_thing_went_wrong));
                                    }
                                } else {
                                    presenterListener.onErrorLogin(message != null ? message : context.getString(R.string.some_thing_went_wrong));
                                }
                            } else {
                                presenterListener.onErrorLogin(message != null ? message : context.getString(R.string.some_thing_went_wrong));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            presenterListener.onErrorLogin(context.getString(R.string.some_thing_went_wrong));
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
                if (isPresenterLive()) {
                    presenterListener.onErrorLogin(presenterListener.getContext().getString(R.string.some_thing_went_wrong));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_POST_USER, username);
                params.put(KEY_POST_PASSWORD, password);
                params.put(KEY_POST_LOGIN_TYPE, loginType);

                Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        if (isPresenterLive()) {
            Context context = presenterListener.getContext();
            if (context != null) {
                VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
            }
        }
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

}
