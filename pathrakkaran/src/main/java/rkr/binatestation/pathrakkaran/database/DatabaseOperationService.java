package rkr.binatestation.pathrakkaran.database;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rkr.binatestation.pathrakkaran.models.AgentProductModel;
import rkr.binatestation.pathrakkaran.models.CompanyMasterModel;
import rkr.binatestation.pathrakkaran.models.ProductMasterModel;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.COLUMN_SAVE_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_USER_TYPE;
import static rkr.binatestation.pathrakkaran.utils.Constants.END_URL_PRODUCTS_SUBSCRIBE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_COMPANIES;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATA;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MESSAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PRODUCT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_STATUS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * This IntentService use to do Database operations in background thread
 */
public class DatabaseOperationService extends IntentService {

    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_ERROR = 2;
    public static final int RESULT_CODE_IN_PROGRESS = 3;

    public static final String KEY_SUCCESS_MESSAGE = "success_message";
    public static final String KEY_ERROR_MESSAGE = "error_message";
    public static final String KEY_IN_PROGRESS_MESSAGE = "in_progress_message";

    // IntentService can perform
    private static final String ACTION_SAVE_MASTERS = "rkr.binatestation.pathrakaran.database.action.SAVE_MASTERS";
    private static final String ACTION_ADD_PRODUCT_AGENT = "rkr.binatestation.pathrakaran.database.action.ADD_PRODUCT_AGENT";
    private static final String ACTION_SAVE_PRODUCT_AGENT = "rkr.binatestation.pathrakaran.database.action.SAVE_PRODUCT_AGENT";
    private static final String ACTION_SAVE_USERS = "rkr.binatestation.pathrakaran.database.action.SAVE_USERS";

    private static final String EXTRA_PARAM1 = "extra_param_1";
    private static final String EXTRA_PARAM2 = "extra_param_2";
    private static final String KEY_RECEIVER = "receiver";
    private static final String TAG = "DatabaseOperationServic";

    private ResultReceiver mResultReceiver;

    public DatabaseOperationService() {
        super("DatabaseOperationService");
    }

    /**
     * Starts this service to perform action Save Masters with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveMasters(Context context, String rootObject) {
        Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(ACTION_SAVE_MASTERS);
        intent.putExtra(EXTRA_PARAM1, rootObject);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action add product agent with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAddProductAgent(Context context, AgentProductModel agentProductModel, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(ACTION_ADD_PRODUCT_AGENT);
        intent.putExtra(EXTRA_PARAM1, agentProductModel);
        intent.putExtra(KEY_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action SAve product agent with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveProductAgent(Context context, String response, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(ACTION_SAVE_PRODUCT_AGENT);
        intent.putExtra(EXTRA_PARAM1, response);
        intent.putExtra(KEY_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Suppliers with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveUsers(Context context, int userType, String response, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, DatabaseOperationService.class);
        intent.setAction(ACTION_SAVE_USERS);
        intent.putExtra(EXTRA_PARAM1, response);
        intent.putExtra(EXTRA_PARAM2, userType);
        intent.putExtra(KEY_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_SAVE_MASTERS: {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                    handleActionSaveMasters(param1);
                }
                break;
                case ACTION_ADD_PRODUCT_AGENT: {
                    final AgentProductModel param1 = intent.getParcelableExtra(EXTRA_PARAM1);
                    mResultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
                    handleActionAddProductAgent(param1);
                }
                break;
                case ACTION_SAVE_PRODUCT_AGENT: {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                    mResultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
                    handleActionSaveProductAgent(param1);
                }
                break;
                case ACTION_SAVE_USERS: {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                    final int userType = intent.getIntExtra(EXTRA_PARAM2, 0);
                    mResultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
                    handleActionSaveUsers(param1, userType);
                }
                break;
            }
        }
    }

    private void handleActionSaveUsers(String param1, int userType) {
        Log.d(TAG, "handleActionSaveUsers() called with: param1 = [" + param1 + "]");
        try {
            JSONObject jsonObject = new JSONObject(param1);
            String message = jsonObject.optString(KEY_MESSAGE);
            Log.d(TAG, "handleActionSaveMasters: " + message);
            if (jsonObject.has(KEY_STATUS) && 200 == jsonObject.optInt(KEY_STATUS)) {
                if (jsonObject.has(KEY_DATA)) {
                    JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
                    if (jsonArray != null && jsonArray.length() > 0) {
                        UserDetailsModel.bulkInsert(getContentResolver(), jsonArray);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Cursor cursor = getContentResolver().query(
                PathrakkaranContract.UserDetailsTable.CONTENT_URI,
                null,
                COLUMN_USER_TYPE + " = ? ",
                new String[]{"" + userType},
                COLUMN_NAME
        );
        if (cursor != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_SUCCESS_MESSAGE, UserDetailsModel.getAll(cursor));
            sendReceiverData(RESULT_CODE_SUCCESS, bundle);
        }
    }

    private void handleActionSaveProductAgent(String param1) {
        Log.d(TAG, "handleActionSaveProductAgent() called with: param1 = [" + param1 + "]");
        try {
            JSONObject jsonObject = new JSONObject(param1);
            String message = jsonObject.optString(KEY_MESSAGE);
            Log.d(TAG, "handleActionSaveMasters: " + message);
            if (jsonObject.has(KEY_STATUS) && 200 == jsonObject.optInt(KEY_STATUS)) {
                if (jsonObject.has(KEY_DATA)) {
                    AgentProductModel.bulkInsert(getContentResolver(), jsonObject.optJSONArray(KEY_DATA));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long userId = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getLong(KEY_USER_ID, 0);
        Cursor cursor = getContentResolver().query(
                PathrakkaranContract.AgentProductListTable.CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                null,
                PathrakkaranContract.AgentProductListTable.COLUMN_AGENT_ID + " = ? ",
                new String[]{"" + userId},
                PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_NAME
        );
        if (cursor != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_SUCCESS_MESSAGE, AgentProductModel.getAgentProductModelList(cursor));
            sendReceiverData(RESULT_CODE_SUCCESS, bundle);
        }
    }

    private void sendReceiverData(int resultCode, Bundle data) {
        if (mResultReceiver != null) {
            mResultReceiver.send(resultCode, data);
        }
    }

    private void handleActionAddProductAgent(final AgentProductModel agentProductModel) {
        Log.d(TAG, "handleActionAddProductAgent() called with: agentProductModel = [" + agentProductModel + "]");
        final Uri uri = AgentProductModel.insert(getContentResolver(), agentProductModel);

        sendReceiverData(RESULT_CODE_SUCCESS, null);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                VolleySingleTon.getDomainUrl() + END_URL_PRODUCTS_SUBSCRIBE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int responseCode = jsonObject.optInt(KEY_STATUS);
                            if (200 == responseCode) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(COLUMN_SAVE_STATUS, 1);
                                getContentResolver().update(
                                        PathrakkaranContract.AgentProductListTable.CONTENT_URI,
                                        contentValues,
                                        _ID + " = ? ",
                                        new String[]{"" + ContentUris.parseId(uri)}
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_AGENT_ID, "" + agentProductModel.getAgentId());
                params.put(KEY_PRODUCT_ID, "" + agentProductModel.getProductId());

                Log.d(TAG, "getParams() returned: " + getUrl() + "  " + params);
                return params;
            }
        };
        VolleySingleTon.getInstance(this).addToRequestQueue(this, stringRequest);

    }

    /**
     * Handle action Save masters in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveMasters(String param1) {
        try {
            JSONObject jsonObject = new JSONObject(param1);
            String message = jsonObject.optString(KEY_MESSAGE);
            Log.d(TAG, "handleActionSaveMasters: " + message);
            if (jsonObject.has(KEY_STATUS) && 200 == jsonObject.optInt(KEY_STATUS)) {
                if (jsonObject.has(KEY_DATA)) {
                    JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_DATA);
                    if (dataJsonObject != null) {
                        JSONArray companiesJsonArray = dataJsonObject.optJSONArray(KEY_COMPANIES);
                        JSONArray productsJsonArray = dataJsonObject.optJSONArray(KEY_PRODUCTS);
                        if (companiesJsonArray != null && companiesJsonArray.length() > 0) {
                            CompanyMasterModel.bulkInsert(getContentResolver(), companiesJsonArray);
                        }
                        if (productsJsonArray != null && productsJsonArray.length() > 0) {
                            ProductMasterModel.bulkInsert(getContentResolver(), productsJsonArray);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
