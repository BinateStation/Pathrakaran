package rkr.binatestation.pathrakkaran.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import rkr.binatestation.pathrakkaran.database.PathrakkaranContract;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.COLUMN_AGENT_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.COLUMN_PRODUCT_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.COLUMN_SAVE_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable.CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AGENT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PRODUCT_ID;

/**
 * Created by RKR on 08/01/2017.
 * AgentProductModel.
 */

public class AgentProductModel implements Parcelable {
    public static final Creator<AgentProductModel> CREATOR = new Creator<AgentProductModel>() {
        @Override
        public AgentProductModel createFromParcel(Parcel in) {
            return new AgentProductModel(in);
        }

        @Override
        public AgentProductModel[] newArray(int size) {
            return new AgentProductModel[size];
        }
    };
    public static final int SAVE_STATUS_SAVED = 1;
    public static final int SAVE_STATUS_NOT_SAVED = 2;
    public static final int SAVE_STATUS_TEMP = 3;
    private static final String TAG = "AgentProductModel";
    private long id;
    private long productId;
    private long agentId;
    private int saveStatus;// save status 1- saved, 2 - For saving, 3 - temp
    private ProductMasterModel productMasterModel;
    private CompanyMasterModel companyMasterModel;

    public AgentProductModel(long productId, long agentId, int saveStatus) {
        this.productId = productId;
        this.agentId = agentId;
        this.saveStatus = saveStatus;
    }

    private AgentProductModel(Parcel in) {
        id = in.readLong();
        productId = in.readLong();
        agentId = in.readLong();
        saveStatus = in.readInt();
        productMasterModel = in.readParcelable(ProductMasterModel.class.getClassLoader());
        companyMasterModel = in.readParcelable(CompanyMasterModel.class.getClassLoader());
    }

    public static long insert(ContentResolver contentResolver, AgentProductModel agentProductModel) {
        Log.d(TAG, "insert() called with: contentResolver = [" + contentResolver + "], agentProductModel = [" + agentProductModel + "]");
        Uri uri = contentResolver.insert(
                CONTENT_URI,
                getContentValues(agentProductModel)
        );
        long insertId = ContentUris.parseId(uri);
        Log.d(TAG, "insert() returned: " + insertId);
        return insertId;
    }

    private static ContentValues getContentValues(AgentProductModel agentProductModel) {
        Log.d(TAG, "getContentValues() called with: agentProductModel = [" + agentProductModel + "]");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AGENT_ID, agentProductModel.getAgentId());
        contentValues.put(COLUMN_PRODUCT_ID, agentProductModel.getProductId());
        contentValues.put(COLUMN_SAVE_STATUS, agentProductModel.getSaveStatus());
        Log.d(TAG, "getContentValues() returned: " + contentValues);
        return contentValues;
    }

    public static CursorLoader getAgentProductModelList(Context context, long agentId) {
        Log.d(TAG, "getAgentProductModelList() called with: context = [" + context + "], agentId = [" + agentId + "]");
        return new CursorLoader(
                context,
                CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                null,
                PathrakkaranContract.AgentProductListTable.COLUMN_AGENT_ID + " = ? ",
                new String[]{"" + agentId},
                PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_NAME
        );
    }

    public static ArrayList<AgentProductModel> getAgentProductModelList(Cursor cursor) {
        Log.d(TAG, "getAgentProductModelList() called with: cursor = [" + cursor + "]");
        ArrayList<AgentProductModel> agentProductModelList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    agentProductModelList.add(cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAgentProductModelList() returned: " + agentProductModelList);
        return agentProductModelList;
    }

    public static ArrayList<AgentProductModel> getAgentProductModelList(ContentResolver contentResolver, long agentId) {
        Log.d(TAG, "getAgentProductModelList() called with: contentResolver = [" + contentResolver + "], agentId = [" + agentId + "]");
        Cursor cursor = contentResolver.query(
                CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                null,
                PathrakkaranContract.AgentProductListTable.COLUMN_AGENT_ID + " = ? ",
                new String[]{"" + agentId},
                PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_NAME
        );
        ArrayList<AgentProductModel> agentProductModelList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    agentProductModelList.add(cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAgentProductModelList() returned: " + agentProductModelList);
        return agentProductModelList;
    }

    private static AgentProductModel cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster(Cursor cursor) {
        Log.d(TAG, "cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster() called with: cursor = [" + cursor + "]");
        AgentProductModel agentProductModel = new AgentProductModel(
                cursor.getLong(cursor.getColumnIndex(COLUMN_PRODUCT_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_AGENT_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SAVE_STATUS))
        );
        agentProductModel.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
        agentProductModel.setProductMasterModel(ProductMasterModel.cursorToProductMasterModel(cursor));
        agentProductModel.setCompanyMasterModel(CompanyMasterModel.cursorToCompanyMasterModel(cursor));
        Log.d(TAG, "cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster() returned: " + agentProductModel);
        return agentProductModel;
    }

    public static int bulkInsert(ContentResolver contentResolver, JSONArray jsonArray) {
        Log.d(TAG, "bulkInsert() called with: contentResolver = [" + contentResolver + "], jsonArray = [" + jsonArray + "]");
        int noOfRowsInserted = contentResolver.bulkInsert(CONTENT_URI, getContentValuesArray(jsonArray));
        Log.d(TAG, "bulkInsert() returned: " + noOfRowsInserted);
        return noOfRowsInserted;
    }

    private static ContentValues[] getContentValuesArray(JSONArray jsonArray) {
        Log.d(TAG, "getContentValuesArray() called with: jsonArray = [" + jsonArray + "]");
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            if (jsonObject != null) {
                contentValues[i] = getContentValues(jsonObject);
            }
        }
        Log.d(TAG, "getContentValuesArray() returned: " + Arrays.toString(contentValues));
        return contentValues;
    }

    private static ContentValues getContentValues(JSONObject jsonObject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AGENT_ID, jsonObject.optLong(KEY_AGENT_ID));
        contentValues.put(COLUMN_PRODUCT_ID, jsonObject.optLong(KEY_PRODUCT_ID));
        contentValues.put(COLUMN_SAVE_STATUS, "1");
        return contentValues;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public int getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(int saveStatus) {
        this.saveStatus = saveStatus;
    }

    public ProductMasterModel getProductMasterModel() {
        return productMasterModel;
    }

    public void setProductMasterModel(ProductMasterModel productMasterModel) {
        this.productMasterModel = productMasterModel;
    }

    public CompanyMasterModel getCompanyMasterModel() {
        return companyMasterModel;
    }

    public void setCompanyMasterModel(CompanyMasterModel companyMasterModel) {
        this.companyMasterModel = companyMasterModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(productId);
        dest.writeLong(agentId);
        dest.writeInt(saveStatus);
        dest.writeParcelable(productMasterModel, flags);
        dest.writeParcelable(companyMasterModel, flags);
    }
}
