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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.pathrakkaran.database.PathrakkaranContract;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_COMPANY_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_IMAGE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_PRICE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_TYPE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_COMPANY_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_IMAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_PRICE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_STATUS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_JSON_PRODUCT_TYPE;

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
    public static final int PRODUCT_TYPE_DAILY = 1;
    public static final int PRODUCT_TYPE_WEEKLY = 2;
    public static final int PRODUCT_TYPE_BIMONTHLY = 3;
    public static final int PRODUCT_TYPE_MONTHLY = 4;
    public static final int PRODUCT_TYPE_YEARLY = 5;
    private static final String TAG = "ProductMasterModel";
    private long id;
    private long productId;
    private long companyId;
    private String productName;
    private String productImage;
    private int productType;
    private double productPrice;
    private double productCost;

    public ProductMasterModel(long productId, long companyId, String productName, String productImage, int productType, double productPrice, double productCost) {
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
        this.productType = in.readInt();
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
        contentValues.put(COLUMN_PRODUCT_TYPE, jsonObject.optInt(KEY_JSON_PRODUCT_TYPE));
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

    public static Map<Long, List<ProductMasterModel>> getAll(Cursor cursor) {
        Log.d(TAG, "getAll() called with: cursor = [" + cursor + "]");
        Map<Long, List<ProductMasterModel>> productMasterModelMap = new LinkedHashMap<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ProductMasterModel productMasterModel = cursorToProductMasterModel(cursor);
                    if (productMasterModelMap.containsKey(productMasterModel.getCompanyId())) {
                        productMasterModelMap.get(productMasterModel.getCompanyId()).add(productMasterModel);
                    } else {
                        List<ProductMasterModel> productMasterModelList = new ArrayList<>();
                        productMasterModelList.add(new ProductMasterModel(0, 0, "Select a Product", "", 0, 0, 0));
                        productMasterModelList.add(productMasterModel);
                        productMasterModelMap.put(productMasterModel.getCompanyId(), productMasterModelList);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAll() returned: " + productMasterModelMap);
        return productMasterModelMap;
    }

    static ProductMasterModel cursorToProductMasterModel(Cursor cursor) {
        Log.d(TAG, "cursorToProductMasterModel() called with: cursor = [" + cursor + "]");
        ProductMasterModel productMasterModel = new ProductMasterModel(
                cursor.getLong(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_ID)),
                cursor.getLong(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_COMPANY_ID)),
                cursor.getString(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_NAME)),
                cursor.getString(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_PRICE)),
                cursor.getDouble(cursor.getColumnIndex(PathrakkaranContract.ProductMasterTable.COLUMN_PRODUCT_COST))
        );

        productMasterModel.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
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

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProductTypeLabel(int productType) {
        switch (productType) {
            case 1:
                return "Daily";
            case 2:
                return "Weekly";
            case 3:
                return "Bimonthly";
            case 4:
                return "Monthly";
            case 5:
                return "Yearly";
            default:
                return "";
        }
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
        dest.writeInt(productType);
        dest.writeDouble(productPrice);
        dest.writeDouble(productCost);
    }

}
