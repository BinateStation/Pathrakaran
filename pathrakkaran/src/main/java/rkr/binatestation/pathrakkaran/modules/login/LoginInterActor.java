package rkr.binatestation.pathrakkaran.modules.login;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static android.content.Context.MODE_PRIVATE;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_USER_LOGIN;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_ADDRESS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATA;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_EMAIL;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_IMAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_IS_LOGGED_IN;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LATITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LOGIN_TYPE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LONGITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MESSAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MOBILE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PASSWORD;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_POSTCODE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_STATUS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ADDRESS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_EMAIL;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_IMAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_LATITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_LONGITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_PHONE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_POSTCODE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_TYPE;

/**
 * Created by RKR on 20/11/2016.
 * LoginInterActor.
 */

class LoginInterActor implements LoginListeners.InterActorListener {

    private static final String TAG = "LoginInterActor";

    private LoginListeners.PresenterListener presenterListener;

    LoginInterActor(LoginListeners.PresenterListener presenterListener) {
        this.presenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return presenterListener != null;
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
                VolleySingleTon.getDomainUrl() + END_URL_USER_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                if (isPresenterLive()) {
                    Context context = presenterListener.getContext();
                    if (context != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.optString(KEY_MESSAGE);
                            if (jsonObject.has(KEY_STATUS) && 200 == jsonObject.optInt(KEY_STATUS)) {
                                if (jsonObject.has(KEY_DATA)) {
                                    JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_DATA);
                                    if (dataJsonObject != null) {
                                        context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit()
                                                .putLong(KEY_USER_ID, dataJsonObject.optLong(KEY_USER_ID))
                                                .putString(KEY_USER_NAME, dataJsonObject.optString(KEY_NAME))
                                                .putString(KEY_USER_ADDRESS, dataJsonObject.optString(KEY_ADDRESS))
                                                .putString(KEY_USER_POSTCODE, dataJsonObject.optString(KEY_POSTCODE))
                                                .putString(KEY_USER_EMAIL, dataJsonObject.optString(KEY_EMAIL))
                                                .putString(KEY_USER_PHONE, dataJsonObject.optString(KEY_MOBILE))
                                                .putString(KEY_USER_IMAGE, dataJsonObject.optString(KEY_IMAGE))
                                                .putInt(KEY_USER_TYPE, dataJsonObject.optInt(KEY_USER_TYPE))
                                                .putString(KEY_LATITUDE, dataJsonObject.optString(KEY_USER_LATITUDE))
                                                .putString(KEY_LONGITUDE, dataJsonObject.optString(KEY_USER_LONGITUDE))
                                                .putBoolean(KEY_IS_LOGGED_IN, true).apply();
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
                params.put(KEY_USER, username);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_LOGIN_TYPE, loginType);

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
}
