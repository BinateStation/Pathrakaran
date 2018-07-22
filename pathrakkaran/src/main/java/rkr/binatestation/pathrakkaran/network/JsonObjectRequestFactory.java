package rkr.binatestation.pathrakkaran.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rkr.binatestation.pathrakkaran.BuildConfig;
import rkr.binatestation.pathrakkaran.network.models.ErrorModel;
import rkr.binatestation.pathrakkaran.utils.Constants;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MESSAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PARAMS;

/**
 * Created by RKR on 22-07-2018.
 * JsonObjectRequestFactory.
 */
@SuppressWarnings("unused")
public class JsonObjectRequestFactory implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String TAG = "JsonObjectRequestFactor";

    private int mRequestMethod = -2;
    private String mMethodName;
    private long mRequestId = 1;
    private JSONObject mParams = new JSONObject();
    private Context mContext;
    private JSONObjectResponseListener mJsonObjectResponseListener;
    private JSONArrayResponseListener mJsonArrayResponseListener;
    private StringResponseListener mStringResponseListener;

    /**
     * Gets new instance for JSONObject request factory.
     *
     * @param methodName the method name of API
     * @return a new object for JsonObjectRequestFactory;
     */
    public static JsonObjectRequestFactory newInstance(String methodName) {
        JsonObjectRequestFactory requestFactory = new JsonObjectRequestFactory();
        requestFactory.setMethodName(methodName);

        return requestFactory;
    }

    /**
     * gets the connection url
     *
     * @return the url where to post the request
     */
    private static String getBaseUrl() {
        return getBaseDomainUrl() + "api/";
    }

    private static String getBaseDomainUrl() {
        if (BuildConfig.DEBUG) {
            return Constants.URL_BASE;
        } else {
            return Constants.URL_BASE_LIVE;
        }
    }

    @SuppressWarnings("unused")
    public void setRequestMethod(int requestMethod) {
        this.mRequestMethod = requestMethod;
    }

    /**
     * Creates StringRequest using the details added in this StringRequestFactory object.
     *
     * @return new object to StringRequest;
     */
    private JsonObjectRequest create() {
        if (mRequestMethod == -2) {
            if (mParams.length() > 0) {
                mRequestMethod = Request.Method.POST;
            } else {
                mRequestMethod = Request.Method.GET;
            }
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                mRequestMethod,
                getBaseUrl() + getMethodName(),
                getInputParam(),
                this,
                this
        );
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "---------------------------------Request---------------------------------------");
            Log.d(TAG, "create: Url :- " + jsonObjectRequest.getUrl());
            Log.d(TAG, "create: Params :- " + mParams);
            Log.d(TAG, "---------------------------------Request End---------------------------------------");
        }
        return jsonObjectRequest;
    }

    private JSONObject getInputParam() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID, getRequestId());
            jsonObject.put(KEY_PARAMS, mParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Method used to do Api call
     *
     * @param context this listener object for listen the result
     */
    public void call(Context context) {
        mContext = context;
        if (context instanceof JSONObjectResponseListener) {
            mJsonObjectResponseListener = (JSONObjectResponseListener) context;
        }
        if (context instanceof JSONArrayResponseListener) {
            mJsonArrayResponseListener = (JSONArrayResponseListener) context;
        }
        if (context instanceof StringResponseListener) {
            mStringResponseListener = (StringResponseListener) context;
        }
        VolleySingleton.getInstance(context).addToRequestQueue(context, create());
    }

    /**
     * Method used to do Api call
     *
     * @param fragment this listener object for listen the result
     */
    public void call(Fragment fragment) {
        mContext = fragment.getContext();
        if (fragment instanceof JSONObjectResponseListener) {
            mJsonObjectResponseListener = (JSONObjectResponseListener) fragment;
        }
        if (fragment instanceof JSONArrayResponseListener) {
            mJsonArrayResponseListener = (JSONArrayResponseListener) fragment;
        }
        if (fragment instanceof StringResponseListener) {
            mStringResponseListener = (StringResponseListener) fragment;
        }
        if (mContext != null) {
            VolleySingleton.getInstance(mContext).addToRequestQueue(mContext, create());
        }
    }


    /**
     * Gets the method name use to post
     *
     * @return the method name
     */
    private String getMethodName() {
        return mMethodName;
    }

    /**
     * Sets the method name for the current request
     *
     * @param methodName the method name
     */
    private void setMethodName(String methodName) {
        this.mMethodName = methodName;
    }

    /**
     * gets the request id to post
     *
     * @return the the request id
     */
    @SuppressWarnings("unused")
    private long getRequestId() {
        return mRequestId;
    }

    /**
     * Sets the unique request id to the request
     *
     * @param requestId the request id
     */
    public void setRequestId(long requestId) {
        this.mRequestId = requestId;
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the String value of parameter
     */
    public void addParam(String key, String value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the float value of parameter
     */
    public void addParam(String key, float value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the long value of parameter
     */
    public void addParam(String key, long value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the int value of parameter
     */
    public void addParam(String key, int value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the double value of parameter
     */
    public void addParam(String key, double value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the boolean value of parameter
     */
    public void addParam(String key, boolean value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the JSONArray value of parameter
     */
    public void addParam(String key, @NonNull JSONArray value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use to add params in to the params json object in the root json object
     *
     * @param key   the key of parameter
     * @param value the JSONObject value of parameter
     */
    public void addParam(String key, @NonNull JSONObject value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "onErrorResponse: ", error);
        ErrorModel errorModel;
        if (mContext != null) {
            if (error instanceof NoConnectionError) {
                errorModel = ErrorModel.getNoConnectionError(mContext);
            } else if (error instanceof TimeoutError) {
                errorModel = ErrorModel.getTimeoutError(mContext);
            } else if (error instanceof NetworkError) {
                errorModel = ErrorModel.getNetworkError(mContext);
            } else {
                errorModel = ErrorModel.getUnKnownError(mContext);
            }
            if (mJsonObjectResponseListener != null) {
                mJsonObjectResponseListener.onErrorResponse(errorModel, mRequestId);
            }
            if (mJsonArrayResponseListener != null) {
                mJsonArrayResponseListener.onErrorResponse(errorModel, mRequestId);
            }
            if (mStringResponseListener != null) {
                mStringResponseListener.onErrorResponse(errorModel, mRequestId);
            }
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResponse: --------------------- Response--------------------------");
            Log.d(TAG, "onResponse() called with: response = [" + response + "]");
            Log.d(TAG, "onResponse: --------------------- Response End ---------------------");
        }
        if (mJsonObjectResponseListener != null) {
            mRequestId = response.optLong(KEY_ID);
            JSONObject dataJsonObject = response.optJSONObject(Constants.KEY_DATA);
            if (dataJsonObject != null) {
                mJsonObjectResponseListener.onResponse(dataJsonObject, mRequestId);
                return;
            }
            String error = response.optString(KEY_MESSAGE);
            if (!TextUtils.isEmpty(error) && mContext != null) {
                mJsonObjectResponseListener.onErrorResponse(
                        new ErrorModel(
                                mContext.getString(android.R.string.dialog_alert_title),
                                error
                        ),
                        mRequestId
                );
                return;
            }
        }
        if (mJsonArrayResponseListener != null) {
            JSONArray dataJsonArray = response.optJSONArray(Constants.KEY_DATA);
            if (dataJsonArray != null) {
                mJsonArrayResponseListener.onResponse(dataJsonArray, mRequestId);
                return;
            }
            String error = response.optString(KEY_MESSAGE);
            if (!TextUtils.isEmpty(error) && mContext != null) {
                mJsonArrayResponseListener.onErrorResponse(
                        new ErrorModel(
                                mContext.getString(android.R.string.dialog_alert_title),
                                error
                        ),
                        mRequestId
                );
                return;
            }
        }

        if (mStringResponseListener != null) {
            String dataString = response.optString(Constants.KEY_DATA);
            if (!TextUtils.isEmpty(dataString)) {
                mStringResponseListener.onResponse(dataString, mRequestId);
                return;
            }
            dataString = response.optString(KEY_MESSAGE);
            if (!TextUtils.isEmpty(dataString)) {
                mStringResponseListener.onErrorResponse(
                        new ErrorModel(
                                mContext.getString(android.R.string.dialog_alert_title),
                                dataString
                        ),
                        mRequestId
                );
                return;
            }
        }
        if (mJsonObjectResponseListener != null && mContext != null) {
            mJsonObjectResponseListener.onErrorResponse(ErrorModel.getUnKnownError(mContext), mRequestId);
            return;
        }

        if (mJsonArrayResponseListener != null && mContext != null) {
            mJsonArrayResponseListener.onErrorResponse(ErrorModel.getUnKnownError(mContext), mRequestId);
            return;
        }

        if (mStringResponseListener != null && mContext != null) {
            mStringResponseListener.onErrorResponse(ErrorModel.getUnKnownError(mContext), mRequestId);
        }
    }
}
