package rkr.binatestation.pathrakkaran.modules.home;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakkaran.database.DatabaseOperationService;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_MASTERS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MASTERS_LAST_UPDATED_DATE;

/**
 * Created by RKR on 8/1/2017.
 * HomeInterActor.
 */

class HomeInterActor implements HomeListeners.InterActorListener {
    private static final String TAG = "HomeInterActor";

    private HomeListeners.PresenterListener mPresenterListener;

    HomeInterActor(HomeListeners.PresenterListener presenterListener) {
        mPresenterListener = presenterListener;
    }

    private boolean isPresenterLive() {
        return mPresenterListener != null;
    }

    @Override
    public void getMasters(final Context context) {
        Log.d(TAG, "getMasters() called");
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                VolleySingleTon.getDomainUrl() + END_URL_MASTERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                        DatabaseOperationService.startActionSaveMasters(context, response);
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
                params.put(KEY_DATE, "" + GeneralUtils.getUnixTimeStamp(context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE)
                        .getLong(KEY_MASTERS_LAST_UPDATED_DATE, 0)));

                Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                return params;
            }
        };
        VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
    }
}
