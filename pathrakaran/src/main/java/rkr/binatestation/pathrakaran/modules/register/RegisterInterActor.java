package rkr.binatestation.pathrakaran.modules.register;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.network.VolleySingleTon;
import rkr.binatestation.pathrakaran.utils.Constants;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.Request.Method.POST;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_DATA;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_EMAIL;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MESSAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MOBILE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_ADDRESS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_EMAIL;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LATITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LOGIN_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LONGITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_MOBILE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_PASSWORD;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_POSTAL_CODE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_IS_LOGGED_IN;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_EMAIL;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_PHONE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_SP_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_REGISTER;

/**
 * Created by RKR on 10/12/2016.
 * RegisterInterActor.
 */

class RegisterInterActor implements RegisterListeners.InterActorListener {

    private static final String TAG = "RegisterInterActor";

    private RegisterListeners.PresenterListener mPresenterListener;

    RegisterInterActor(RegisterListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void register(final Context context, final String name, final String phone, final String email, final String password, final String userType, final String loginType) {
        Log.d(TAG, "register() called with: context = [" + context + "], name = [" + name + "], phone = [" + phone + "], email = [" + email + "], password = [" + password + "], userType = [" + userType + "], loginType = [" + loginType + "]");
        StringRequest stringRequest = new StringRequest(
                POST,
                VolleySingleTon.getDomainUrl() + USER_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (200 == jsonObject.optInt(Constants.KEY_JSON_STATUS)) {
                                JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_JSON_DATA);
                                if (dataJsonObject != null) {
                                    context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).edit()
                                            .putString(KEY_SP_USER_ID, dataJsonObject.getString(KEY_JSON_USER_ID))
                                            .putString(KEY_SP_USER_NAME, dataJsonObject.getString(KEY_JSON_NAME))
                                            .putString(KEY_SP_USER_EMAIL, dataJsonObject.getString(KEY_JSON_EMAIL))
                                            .putString(KEY_SP_USER_PHONE, dataJsonObject.getString(KEY_JSON_MOBILE))
                                            .putString(KEY_SP_USER_IMAGE, dataJsonObject.getString(KEY_JSON_IMAGE))
                                            .putString(KEY_SP_USER_TYPE, dataJsonObject.getString(KEY_JSON_USER_TYPE))
                                            .putBoolean(KEY_SP_IS_LOGGED_IN, true).apply();
                                    if (isPresenterLive()) {
                                        mPresenterListener.registerSuccessfully();
                                    }
                                } else {
                                    if (isPresenterLive()) {
                                        mPresenterListener.errorRegistering(
                                                jsonObject.has(KEY_JSON_MESSAGE) ?
                                                        jsonObject.optString(KEY_JSON_MESSAGE) :
                                                        "Something went wrong, please try again later.!"
                                        );
                                    }
                                }
                            } else {
                                if (isPresenterLive()) {
                                    mPresenterListener.errorRegistering(
                                            jsonObject.has(KEY_JSON_MESSAGE) ?
                                                    jsonObject.optString(KEY_JSON_MESSAGE) :
                                                    "Something went wrong, please try again later.!"
                                    );
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (isPresenterLive()) {
                                mPresenterListener.errorRegistering("Something went wrong, please try again later.!");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        if (isPresenterLive()) {
                            mPresenterListener.errorRegistering("Username not available, try another one");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_POST_NAME, name);
                params.put(KEY_POST_MOBILE, phone);
                params.put(KEY_POST_PASSWORD, password);
                params.put(KEY_POST_USER_TYPE, userType);
                params.put(KEY_POST_ADDRESS, "");
                params.put(KEY_POST_POSTAL_CODE, "");
                params.put(KEY_POST_EMAIL, email);
                params.put(KEY_POST_LATITUDE, "");
                params.put(KEY_POST_LONGITUDE, "");
                params.put(KEY_POST_LOGIN_TYPE, loginType);


                Log.d(TAG, "getParams() returned: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
    }
}
