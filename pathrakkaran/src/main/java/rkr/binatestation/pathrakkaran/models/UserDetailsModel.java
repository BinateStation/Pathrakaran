package rkr.binatestation.pathrakkaran.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_ADDRESS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_EMAIL;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_IMAGE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_LATITUDE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_LONGITUDE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_MOBILE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_NAME;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_POSTCODE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_SAVE_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_USER_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.COLUMN_USER_TYPE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_ADDRESS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_EMAIL;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_IMAGE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LATITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_LONGITUDE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_MOBILE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_NAME;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_POSTCODE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_TYPE;

/**
 * Created by RKR on 11/12/2016.
 * UserDetailsModel.
 */

public class UserDetailsModel implements Parcelable {
    public static final Parcelable.Creator<UserDetailsModel> CREATOR = new Parcelable.Creator<UserDetailsModel>() {
        @Override
        public UserDetailsModel createFromParcel(Parcel in) {
            return new UserDetailsModel(in);
        }

        @Override
        public UserDetailsModel[] newArray(int size) {
            return new UserDetailsModel[size];
        }
    };
    public static final int USER_TYPE_AGENT = 1;
    public static final int USER_TYPE_SUPPLIER = 2;
    public static final int USER_TYPE_SUBSCRIBER = 3;
    public static final int SAVE_STATUS_SAVED = 1;
    public static final int SAVE_STATUS_NOT_SAVED = 2;
    private static final String TAG = "UserDetailsModel";
    private long id;
    private long userId;
    private String name;
    private String address;
    private String postcode;
    private String email;
    private String mobile;
    private String image;
    private int userType;//1 Agent,2 Supplier,3 Agent;
    private double latitude;
    private double longitude;
    private int saveStatus;

    public UserDetailsModel(long userId, String name, String address, String postcode, String email, String mobile, String image, int userType, double latitude, double longitude, int saveStatus) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.saveStatus = saveStatus;
    }

    private UserDetailsModel(Parcel in) {
        id = in.readLong();
        userId = in.readLong();
        name = in.readString();
        address = in.readString();
        postcode = in.readString();
        email = in.readString();
        mobile = in.readString();
        image = in.readString();
        userType = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        saveStatus = in.readInt();
    }

    public static UserDetailsModel getDefault() {
        return new UserDetailsModel(
                new Random().nextLong(),
                "Select User",
                "",
                "",
                "",
                "",
                "",
                USER_TYPE_SUBSCRIBER,
                0,
                0,
                SAVE_STATUS_NOT_SAVED
        );
    }

    public static ArrayList<UserDetailsModel> getAll(Cursor cursor) {
        Log.d(TAG, "getAll() called with: cursor = [" + cursor + "]");
        ArrayList<UserDetailsModel> userDetailsModelList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    userDetailsModelList.add(cursorToUserDetailsModel(cursor));
                } while (cursor.moveToNext());
            }
        }
        Log.d(TAG, "getAll() returned: " + userDetailsModelList);
        return userDetailsModelList;
    }

    public static int bulkInsert(ContentResolver contentResolver, JSONArray jsonArray) {
        Log.d(TAG, "bulkInsert() called with: contentResolver = [" + contentResolver + "], jsonArray = [" + jsonArray + "]");
        int noOfRowsInserted = contentResolver.bulkInsert(CONTENT_URI, getContentValuesArray(jsonArray));
        Log.d(TAG, "bulkInsert() returned: " + noOfRowsInserted);
        return noOfRowsInserted;
    }

    public static long insert(ContentResolver contentResolver, UserDetailsModel userDetailsModel) {
        Log.d(TAG, "insert() called with: contentResolver = [" + contentResolver + "], userDetailsModel = [" + userDetailsModel + "]");
        Uri uri = contentResolver.insert(
                CONTENT_URI,
                getContentValues(userDetailsModel)
        );
        long insertId = ContentUris.parseId(uri);
        Log.d(TAG, "insert() returned: " + insertId);
        return insertId;
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
        contentValues.put(COLUMN_USER_ID, jsonObject.optLong(KEY_USER_ID));
        contentValues.put(COLUMN_NAME, jsonObject.optString(KEY_NAME));
        contentValues.put(COLUMN_ADDRESS, jsonObject.optString(KEY_ADDRESS));
        contentValues.put(COLUMN_POSTCODE, jsonObject.optString(KEY_POSTCODE));
        contentValues.put(COLUMN_EMAIL, jsonObject.optString(KEY_EMAIL));
        contentValues.put(COLUMN_MOBILE, jsonObject.optString(KEY_MOBILE));
        contentValues.put(COLUMN_IMAGE, jsonObject.optString(KEY_IMAGE));
        contentValues.put(COLUMN_USER_TYPE, jsonObject.optInt(KEY_USER_TYPE));
        contentValues.put(COLUMN_LATITUDE, jsonObject.optDouble(KEY_LATITUDE));
        contentValues.put(COLUMN_LONGITUDE, jsonObject.optDouble(KEY_LONGITUDE));
        contentValues.put(COLUMN_SAVE_STATUS, SAVE_STATUS_SAVED);
        return contentValues;
    }

    private static ContentValues getContentValues(UserDetailsModel userDetailsModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID, userDetailsModel.getUserId());
        contentValues.put(COLUMN_NAME, userDetailsModel.getName());
        contentValues.put(COLUMN_ADDRESS, userDetailsModel.getAddress());
        contentValues.put(COLUMN_POSTCODE, userDetailsModel.getPostcode());
        contentValues.put(COLUMN_EMAIL, userDetailsModel.getEmail());
        contentValues.put(COLUMN_MOBILE, userDetailsModel.getMobile());
        contentValues.put(COLUMN_IMAGE, userDetailsModel.getImage());
        contentValues.put(COLUMN_USER_TYPE, userDetailsModel.getUserType());
        contentValues.put(COLUMN_LATITUDE, userDetailsModel.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, userDetailsModel.getLongitude());
        contentValues.put(COLUMN_SAVE_STATUS, userDetailsModel.getSaveStatus());
        return contentValues;
    }

    private static UserDetailsModel cursorToUserDetailsModel(Cursor cursor) {
        Log.d(TAG, "cursorToUserDetailsModel() called with: cursor = [" + cursor + "]");
        UserDetailsModel userDetailsModel = new UserDetailsModel(
                cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_POSTCODE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_USER_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SAVE_STATUS))
        );
        userDetailsModel.setId(cursor.getLong(0));
        Log.d(TAG, "cursorToUserDetailsModel() returned: " + userDetailsModel);
        return userDetailsModel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(int saveStatus) {
        this.saveStatus = saveStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(postcode);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(image);
        dest.writeInt(userType);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(saveStatus);
    }
}
