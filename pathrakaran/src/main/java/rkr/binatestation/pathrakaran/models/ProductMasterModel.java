package rkr.binatestation.pathrakaran.models;

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

import rkr.binatestation.pathrakaran.database.PathrakaranContract;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_COMPANY_ID;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_ID;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_IMAGE;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_NAME;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_PRICE;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_STATUS;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_TYPE;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable.CONTENT_URI;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_COMPANY_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_ID;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_IMAGE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_NAME;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_PRICE;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_STATUS;
import static rkr.binatestation.pathrakaran.utils.Constants.KEY_JSON_PRODUCT_TYPE;

/**
 * Created by RKR on 8/1/2017.
 * ProductMasterModel.
 */

public class ProductMasterModel implements Parcelable {
    public static final Creator<ProductMasterModel> CREATOR = new Creator<ProductMasterModel>() {
        @Override
        public ProductMasterModel createFromParcel(Parcel in) {
            return new ProductMasterModel(in);
        }

        @Override
        public ProductMasterModel[] newArray(int size) {
            return new ProductMasterModel[size];
        }
    };
    private static final String TAG = "ProductMasterModel";
    private long id;
    private long productId;
    private long companyId;
    private String productName;
    private String productImage;
    private String productType;
    private double productPrice;
    private double productCost;

    public ProductMasterModel(long productId, long companyId, String productName, String productImage, String productType, double productPrice, double productCost) {
        this.productId = productId;
        this.companyId = companyId;
        this.productName = productName;
        this.productImage = productImage;
        this.productType = productType;
        this.productPrice = productPrice;
        this.productCost = productCost;
    }

    private ProductMasterModel(Parcel in) {
        this.id = in.readLong();
        this.productId = in.readLong();
        this.companyId = in.readLong();
        this.productName = in.readString();
        this.productImage = in.readString();
        this.productType = in.readString();
        this.productPrice = in.readDouble();
        this.productCost = in.readDouble();
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
        contentValues.put(COLUMN_PRODUCT_ID, jsonObject.optInt(KEY_JSON_PRODUCT_ID));
        contentValues.put(COLUMN_COMPANY_ID, jsonObject.optInt(KEY_JSON_COMPANY_ID));
        contentValues.put(COLUMN_PRODUCT_NAME, jsonObject.optString(KEY_JSON_PRODUCT_NAME));
        contentValues.put(COLUMN_PRODUCT_PRICE, jsonObject.optInt(KEY_JSON_PRODUCT_PRICE));
        contentValues.put(COLUMN_PRODUCT_IMAGE, jsonObject.optString(KEY_JSON_PRODUCT_IMAGE));
        contentValues.put(COLUMN_PRODUCT_TYPE, jsonObject.optString(KEY_JSON_PRODUCT_TYPE));
        contentValues.put(COLUMN_PRODUCT_STATUS, jsonObject.optInt(KEY_JSON_PRODUCT_STATUS));
        Log.d(TAG, "getContentValues() returned: " + contentValues);
        return contentValues;
    }

    public static int bulkInsert(ContentResolver contentResolver, JSONArray jsonArray) {
        Log.d(TAG, "bulkInsert() called with: contentResolver = [" + contentResolver + "], jsonArray = [" + jsonArray + "]");
        int noOfRowsInserted = contentResolver.bulkInsert(
                CONTENT_URI,
                getContentValuesArray(jsonArray)
        );
        Log.d(TAG, "bulkInsert() returned: " + noOfRowsInserted);
        return noOfRowsInserted;
    }

    public static List<ProductMasterModel> getAll(Cursor cursor) {
        Log.d(TAG, "getAll() called with: cursor = [" + cursor + "]");
        List<ProductMasterModel> productMasterModelList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    productMasterModelList.add(cursorToProductMasterModel(cursor, false));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAll() returned: " + productMasterModelList);
        return productMasterModelList;
    }

    public static ProductMasterModel cursorToProductMasterModel(Cursor cursor, boolean isJoined) {
        Log.d(TAG, "cursorToProductMasterModel() called with: cursor = [" + cursor + "]");
        ProductMasterModel productMasterModel = new ProductMasterModel(
                cursor.getLong(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_ID)),
                cursor.getLong(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_COMPANY_ID)),
                cursor.getString(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_NAME)),
                cursor.getString(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_IMAGE)),
                cursor.getString(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_PRICE)),
                cursor.getDouble(cursor.getColumnIndex(PathrakaranContract.ProductMasterTable.COLUMN_PRODUCT_COST))
        );
        if (isJoined) {
            productMasterModel.setId(cursor.getLong(cursor.getColumnIndex("PM." + _ID)));
        } else {
            productMasterModel.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
        }
        Log.d(TAG, "cursorToProductMasterModel() returned: " + productMasterModel);
        return productMasterModel;
    }

    @Override
    public String toString() {
        return getProductName();
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

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getProductCost() {
        return productCost;
    }

    public void setProductCost(double productCost) {
        this.productCost = productCost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(productId);
        dest.writeLong(companyId);
        dest.writeString(productName);
        dest.writeString(productImage);
        dest.writeString(productType);
        dest.writeDouble(productPrice);
        dest.writeDouble(productCost);
    }

}
