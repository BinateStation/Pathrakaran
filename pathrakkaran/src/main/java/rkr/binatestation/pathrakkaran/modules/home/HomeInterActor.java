package rkr.binatestation.pathrakkaran.modules.home;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import rkr.binatestation.pathrakkaran.database.DatabaseOperationService;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_MASTERS;

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
        );
        VolleySingleTon.getInstance(context).addToRequestQueue(context, stringRequest);
    }
}
