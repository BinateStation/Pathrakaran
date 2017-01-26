package rkr.binatestation.pathrakkaran.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_LOGO;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable.COLUMN_COMPANY_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_COMPANY_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_COMPANY_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_COMPANY_STATUS;

/**
 * Created by RKR on 8/1/2017.
 * CompanyMasterModel.
 */

public class CompanyMasterModel implements Parcelable {
    public static final Creator<CompanyMasterModel> CREATOR = new Creator<CompanyMasterModel>() {
        @Override
        public CompanyMasterModel createFromParcel(Parcel in) {
            return new CompanyMasterModel(in);
        }

        @Override
        public CompanyMasterModel[] newArray(int size) {
            return new CompanyMasterModel[size];
        }
    };
    private static final String TAG = "CompanyMasterModel";
    private long id;
    private long companyId;
    private String companyName;
    private String companyLogo;
    private int companyStatus;

    public CompanyMasterModel(long companyId, String companyName, String companyLogo, int companyStatus) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyLogo = companyLogo;
        this.companyStatus = companyStatus;
    }

    private CompanyMasterModel(Parcel in) {
        id = in.readLong();
        companyId = in.readLong();
        companyName = in.readString();
        companyLogo = in.readString();
        companyStatus = in.readInt();
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
        Log.d(TAG, "getContentValues() called with: jsonObject = [" + jsonObject + "]");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COMPANY_ID, jsonObject.optInt(KEY_JSON_COMPANY_ID));
        contentValues.put(COLUMN_COMPANY_NAME, jsonObject.optString(KEY_JSON_COMPANY_NAME));
        contentValues.put(COLUMN_COMPANY_STATUS, jsonObject.optInt(KEY_JSON_COMPANY_STATUS));
        Log.d(TAG, "getContentValues() returned: " + contentValues);
        return contentValues;
    }

    public static int bulkInsert(ContentResolver contentResolver, JSONArray jsonArray) {
        Log.d(TAG, "bulkInsert() called with: contentResolver = [" + contentResolver + "], jsonArray = [" + jsonArray + "]");
        int noOfRowsInserted = contentResolver.bulkInsert(CONTENT_URI, getContentValuesArray(jsonArray));
        Log.d(TAG, "bulkInsert() returned: " + noOfRowsInserted);
        return noOfRowsInserted;
    }

    public static List<CompanyMasterModel> getAll(Cursor cursor) {
        Log.d(TAG, "getAll() called with: cursor = [" + cursor + "]");
        List<CompanyMasterModel> companyMasterModelList = new ArrayList<>();
        companyMasterModelList.add(new CompanyMasterModel(0, "Select a Company", "", 0));
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    companyMasterModelList.add(cursorToCompanyMasterModel(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAll() returned: " + companyMasterModelList);
        return companyMasterModelList;
    }

    static CompanyMasterModel cursorToCompanyMasterModel(Cursor cursor) {
        Log.d(TAG, "cursorToCompanyMasterModel() called with: cursor = [" + cursor + "]");
        CompanyMasterModel companyMasterModel = new CompanyMasterModel(
                cursor.getLong(cursor.getColumnIndex(COLUMN_COMPANY_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_LOGO)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_STATUS))
        );
        companyMasterModel.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
        Log.d(TAG, "cursorToCompanyMasterModel() returned: " + companyMasterModel);
        return companyMasterModel;
    }

    @Override
    public String toString() {
        return getCompanyName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public int getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(int companyStatus) {
        this.companyStatus = companyStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(companyId);
        dest.writeString(companyName);
        dest.writeString(companyLogo);
        dest.writeInt(companyStatus);
    }
}
