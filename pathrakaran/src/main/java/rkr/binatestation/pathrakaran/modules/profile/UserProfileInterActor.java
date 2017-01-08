package rkr.binatestation.pathrakaran.modules.profile;

import android.content.Context;
import android.util.Log;

import com.alexbbb.uploadservice.ContentType;
import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakaran.R;
import rkr.binatestation.pathrakaran.models.UserDetailsModel;
import rkr.binatestation.pathrakaran.network.VolleySingleTon;
import rkr.binatestation.pathrakaran.utils.Constants;

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
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_USER_TYPE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_ADDRESS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LATITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_LONGITUDE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_POSTCODE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_POST_USER_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.USER_PROFILE_UPDATE;

/**
 * Created by RKR on 11/12/2016.
 * UserProfileInterActor.
 */

class UserProfileInterActor implements UserProfileListeners.InterActorListener {
    private static final String TAG = "UserProfileInterActor";
    private UserProfileListeners.PresenterListener mPresenterListener;

    UserProfileInterActor(UserProfileListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void getUserDetails(Context context, final String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                VolleySingleTon.getDomainUrl() + Constants.USER_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (200 == jsonObject.optInt(Constants.KEY_JSON_STATUS)) {
                        JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_JSON_DATA);
                        if (dataJsonObject != null) {
                            if (isPresenterLive()) {
                                mPresenterListener.setUserData(new UserDetailsModel(
                                        dataJsonObject.optLong(KEY_JSON_USER_ID),
                                        dataJsonObject.optString(KEY_JSON_NAME),
                                        dataJsonObject.optString(KEY_JSON_ADDRESS),
                                        dataJsonObject.optString(KEY_JSON_POSTCODE),
                                        dataJsonObject.optString(KEY_JSON_EMAIL),
                                        dataJsonObject.optString(KEY_JSON_MOBILE),
                                        dataJsonObject.optString(KEY_JSON_IMAGE),
                                        dataJsonObject.optString(KEY_JSON_USER_TYPE),
                                        dataJsonObject.optDouble(KEY_JSON_LATITUDE),
                                        dataJsonObject.optDouble(KEY_JSON_LONGITUDE)

                                ));
                            }
                        } else {
                            if (isPresenterLive()) {
                                mPresenterListener.errorGettingUserDetails(
                                        jsonObject.has(KEY_JSON_MESSAGE) ?
                                                jsonObject.optString(KEY_JSON_MESSAGE) :
                                                "Something went wrong, please try again later.!"
                                );
                            }
                        }
                    } else {
                        if (isPresenterLive()) {
                            mPresenterListener.errorGettingUserDetails(
                                    jsonObject.has(KEY_JSON_MESSAGE) ?
                                            jsonObject.optString(KEY_JSON_MESSAGE) :
                                            "Something went wrong, please try again later.!"
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (isPresenterLive()) {
                        mPresenterListener.errorGettingUserDetails("Something went wrong, please try again later.!");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
                if (isPresenterLive()) {
                    mPresenterListener.errorGettingUserDetails("Network error please try again later.!");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_POST_USER_ID, userId);
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

    public void updateUserDetails(Context context, final long userId, final String name, final String address, final String postcode, final String latitude, final String longitude, String imagePath) {
        updateUserDetailsWithImage(context, userId, name, address, postcode, latitude, longitude, imagePath);
    }

    private void updateUserDetailsWithImage(final Context context, long userId, String name, String address, String postcode, String latitude, String longitude, String imagePath) {
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) {
                MultipartUploadRequest request = new MultipartUploadRequest(context, file.getName(), VolleySingleTon.getDomainUrl() + Constants.USER_PROFILE_UPDATE);
                request.addFileToUpload(imagePath, KEY_POST_IMAGE, file.getName(), ContentType.IMAGE_JPEG);
                request.addParameter(KEY_POST_USER_ID, "" + userId);
                request.addParameter(KEY_POST_NAME, name);
                request.addParameter(KEY_POST_ADDRESS, address);
                request.addParameter(KEY_POST_POSTCODE, postcode);
                request.addParameter(KEY_POST_LATITUDE, latitude);
                request.addParameter(KEY_POST_LONGITUDE, longitude);

                //configure the notification
                request.setNotificationConfig(R.drawable.ic_my_library_books_24dp,
                        context.getString(R.string.app_name),
                        " Profile update in progress ",
                        " Profile update completed successfully",
                        " Profile update Intercepted",
                        true);

                // if you comment the following line, the system default user-agent will be used
                request.setCustomUserAgent("UploadServiceDemo/1.0");


                // set the maximum number of automatic upload retries on error
                request.setMaxRetries(2);

                try {
                    Log.i(TAG, "Request :- " + request.toString());
                    request.startUpload();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else {
                updateUserDetailsWithoutImage(context, userId, name, address, postcode, latitude, longitude);
            }
        } else {
            updateUserDetailsWithoutImage(context, userId, name, address, postcode, latitude, longitude);
        }
    }

    private void updateUserDetailsWithoutImage(Context context, final long userId, final String name, final String address, final String postcode, final String latitude, final String longitude) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                VolleySingleTon.getDomainUrl() + USER_PROFILE_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (200 == jsonObject.optInt(Constants.KEY_JSON_STATUS)) {
                                JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_JSON_DATA);
                                if (dataJsonObject != null) {
                                    if (isPresenterLive()) {
                                        mPresenterListener.setUserData(new UserDetailsModel(
                                                dataJsonObject.optLong(KEY_JSON_USER_ID),
                                                dataJsonObject.optString(KEY_JSON_NAME),
                                                dataJsonObject.optString(KEY_JSON_ADDRESS),
                                                dataJsonObject.optString(KEY_JSON_POSTCODE),
                                                dataJsonObject.optString(KEY_JSON_EMAIL),
                                                dataJsonObject.optString(KEY_JSON_MOBILE),
                                                dataJsonObject.optString(KEY_JSON_IMAGE),
                                                dataJsonObject.optString(KEY_JSON_USER_TYPE),
                                                dataJsonObject.optDouble(KEY_JSON_LATITUDE),
                                                dataJsonObject.optDouble(KEY_JSON_LONGITUDE)

                                        ));
                                    }
                                } else {
                                    if (isPresenterLive()) {
                                        mPresenterListener.errorGettingUserDetails(
                                                jsonObject.has(KEY_JSON_MESSAGE) ?
                                                        jsonObject.optString(KEY_JSON_MESSAGE) :
                                                        "Something went wrong, please try again later.!"
                                        );
                                    }
                                }
                            } else {
                                if (isPresenterLive()) {
                                    mPresenterListener.errorGettingUserDetails(
                                            jsonObject.has(KEY_JSON_MESSAGE) ?
                                                    jsonObject.optString(KEY_JSON_MESSAGE) :
                                                    "Something went wrong, please try again later.!"
                                    );
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (isPresenterLive()) {
                                mPresenterListener.errorGettingUserDetails("Something went wrong, please try again later.!");
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        if (isPresenterLive()) {
                            mPresenterListener.errorGettingUserDetails("Network error please try again later.!");
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_POST_USER_ID, "" + userId);
                params.put(KEY_POST_NAME, name);
                params.put(KEY_POST_ADDRESS, address);
                params.put(KEY_POST_POSTCODE, postcode);
                params.put(KEY_POST_LATITUDE, latitude);
                params.put(KEY_POST_LONGITUDE, longitude);

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
