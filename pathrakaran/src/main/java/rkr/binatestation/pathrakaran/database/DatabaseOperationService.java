package rkr.binatestation.pathrakaran.database;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rkr.binatestation.pathrakaran.models.CompanyMasterModel;
import rkr.binatestation.pathrakaran.models.ProductMasterModel;

import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_COMPANIES;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_DATA;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_MESSAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCTS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_STATUS;

/**
 * This IntentService use to do Database operations in background thread
 */
public class DatabaseOperationService extends IntentService {
    // IntentService can perform
    private static final String ACTION_SAVE_MASTERS = "rkr.binatestation.pathrakaran.database.action.SAVE_MASTERS";

    private static final String EXTRA_PARAM1 = "extra_param_1";
    private static final String TAG = "DatabaseOperationServic";

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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_SAVE_MASTERS: {
                    final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                    handleActionSaveMasters(param1);
                }
                break;
            }
        }
    }

    /**
     * Handle action Save masters in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveMasters(String param1) {
        try {
            JSONObject jsonObject = new JSONObject(param1);
            String message = jsonObject.optString(KEY_JSON_MESSAGE);
            Log.d(TAG, "handleActionSaveMasters: " + message);
            if (jsonObject.has(KEY_JSON_STATUS) && 200 == jsonObject.optInt(KEY_JSON_STATUS)) {
                if (jsonObject.has(KEY_JSON_DATA)) {
                    JSONObject dataJsonObject = jsonObject.optJSONObject(KEY_JSON_DATA);
                    if (dataJsonObject != null) {
                        JSONArray companiesJsonArray = dataJsonObject.optJSONArray(KEY_JSON_COMPANIES);
                        JSONArray productsJsonArray = dataJsonObject.optJSONArray(KEY_JSON_PRODUCTS);
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
