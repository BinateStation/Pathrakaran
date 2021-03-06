package rkr.binatestation.pathrakkaran.modules.register;

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

import rkr.binatestation.pathrakkaran.network.VolleySingleTon;
import rkr.binatestation.pathrakkaran.utils.Constants;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.Request.Method.POST;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_USER_REGISTER;
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
    public void register(final Context context, final String name, final String phone, final String email, final String password, final int userType, final String loginType) {
        Log.d(TAG, "register() called with: context = [" + context + "], name = [" + name + "], phone = [" + phone + "], email = [" + email + "], password = [" + password + "], userType = [" + userType + "], loginType = [" + loginType + "]");
        StringRequest stringRequest = new StringRequest(
                POST,
                VolleySingleTon.getDomainUrl() + END_URL_USER_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (200 == jsonObject.optInt(Constants.KEY_STATUS)) {
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
                                    if (isPresenterLive()) {
                                        mPresenterListener.registerSuccessfully();
                                    }
                                } else {
                                    if (isPresenterLive()) {
                                        mPresenterListener.errorRegistering(
                                                jsonObject.has(KEY_MESSAGE) ?
                                                        jsonObject.optString(KEY_MESSAGE) :
                                                        "Something went wrong, please try again later.!"
                                        );
                                    }
                                }
                            } else {
                                if (isPresenterLive()) {
                                    mPresenterListener.errorRegistering(
                                            jsonObject.has(KEY_MESSAGE) ?
                                                    jsonObject.optString(KEY_MESSAGE) :
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
                params.put(KEY_NAME, name);
                params.put(KEY_MOBILE, phone);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_USER_TYPE, "" + userType);
                params.put(KEY_ADDRESS, "");
                params.put(KEY_POSTCODE, "");
                params.put(KEY_EMAIL, email);
                params.put(KEY_LATITUDE, "");
                params.put(KEY_LONGITUDE, "");
                params.put(KEY_LOGIN_TYPE, loginType);


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
