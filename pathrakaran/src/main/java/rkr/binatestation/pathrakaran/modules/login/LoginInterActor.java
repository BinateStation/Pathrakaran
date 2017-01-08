package rkr.binatestation.pathrakaran.modules.login;

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

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;

import static android.content.Context.MODE_PRIVATE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_ADDRESS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_DATA;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_EMAIL;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_LATITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_LONGITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MESSAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MOBILE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_POSTCODE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_STATUS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LOGIN_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_MOBILE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_PASSWORD;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_IS_LOGGED_IN;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ADDRESS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_EMAIL;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_LATITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_LONGITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_PHONE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_POSTCODE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_LOGIN;

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
                                                .putLong(KEY_SP_USER_ID, dataJsonObject.optLong(KEY_JSON_USER_ID))
                                                .putString(KEY_SP_USER_NAME, dataJsonObject.optString(KEY_JSON_NAME))
                                                .putString(KEY_SP_USER_ADDRESS, dataJsonObject.optString(KEY_JSON_ADDRESS))
                                                .putString(KEY_SP_USER_POSTCODE, dataJsonObject.optString(KEY_JSON_POSTCODE))
                                                .putString(KEY_SP_USER_EMAIL, dataJsonObject.optString(KEY_JSON_EMAIL))
                                                .putString(KEY_SP_USER_PHONE, dataJsonObject.optString(KEY_JSON_MOBILE))
                                                .putString(KEY_SP_USER_IMAGE, dataJsonObject.optString(KEY_JSON_IMAGE))
                                                .putString(KEY_SP_USER_TYPE, dataJsonObject.optString(KEY_JSON_USER_TYPE))
                                                .putString(KEY_JSON_LATITUDE, dataJsonObject.optString(KEY_SP_USER_LATITUDE))
                                                .putString(KEY_JSON_LONGITUDE, dataJsonObject.optString(KEY_SP_USER_LONGITUDE))
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
                params.put(KEY_POST_MOBILE, username);
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
}
