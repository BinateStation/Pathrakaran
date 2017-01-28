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
import java.util.Random;

import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_AMOUNT;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_DATE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_PAYEE;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_PAYER;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_SAVE_STATUS;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.COLUMN_TRANSACTION_ID;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.CONTENT_URI;
import static rkr.binatestation.pathrakkaran.database.PathrakkaranContract.TransactionsTable.CONTENT_URI_JOIN_USER_DETAILS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_AMOUNT;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_DATE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PAYEE;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_PAYER;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_TRANSACTION_ID;

/**
 * Created by RKR on 11/12/2016.
 * TransactionModel.
 */

public class TransactionModel implements Parcelable {
    public static final Creator<TransactionModel> CREATOR = new Creator<TransactionModel>() {
        @Override
        public TransactionModel createFromParcel(Parcel in) {
            return new TransactionModel(in);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };
    public static final int SAVE_STATUS_SAVED = 1;
    public static final int SAVE_STATUS_NOT_SAVED = 2;
    private static final String TAG = "TransactionModel";
    private UserDetailsModel userDetailsModel;
    private long id;
    private long transactionId;
    private long payer;
    private long payee;
    private double amount;
    private long date;
    private int saveStatus;

    private TransactionModel(long transactionId, long payer, long payee, double amount, long date, int saveStatus) {
        this.transactionId = transactionId;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.date = date;
        this.saveStatus = saveStatus;
    }

    private TransactionModel(Parcel in) {
        id = in.readLong();
        transactionId = in.readLong();
        payer = in.readLong();
        payee = in.readLong();
        amount = in.readLong();
        date = in.readLong();
        saveStatus = in.readInt();
        userDetailsModel = in.readParcelable(UserDetailsModel.class.getClassLoader());
    }

    public static TransactionModel getDefault() {
        return new TransactionModel(
                new Random().nextLong(),
                0,
                0,
                0,
                0,
                SAVE_STATUS_NOT_SAVED
        );
    }

    public static CursorLoader getAllWithUserDetails(Context context) {
        Log.d(TAG, "getAll() called with: context = [" + context + "]");
        return new CursorLoader(
                context,
                CONTENT_URI_JOIN_USER_DETAILS,
                null,
                null,
                null,
                COLUMN_DATE + " DESC "
        );
    }

    public static ArrayList<TransactionModel> getAllWithUserDetails(Cursor cursor) {
        Log.d(TAG, "getAll() called with: cursor = [" + cursor + "]");
        ArrayList<TransactionModel> transactionModelArrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    transactionModelArrayList.add(cursorToTransactionModel(cursor, true));
                } while (cursor.moveToNext());
            }
        }
        Log.d(TAG, "getAll() returned: " + transactionModelArrayList);
        return transactionModelArrayList;
    }

    public static ArrayList<TransactionModel> getAllWithUserDetails(ContentResolver contentResolver) {
        Log.d(TAG, "getAll() called with: contentResolver = [" + contentResolver + "]");
        Cursor cursor = contentResolver.query(
                CONTENT_URI_JOIN_USER_DETAILS,
                null,
                null,
                null,
                COLUMN_DATE + " DESC "
        );
        ArrayList<TransactionModel> transactionModelArrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    transactionModelArrayList.add(cursorToTransactionModel(cursor, true));
                } while (cursor.moveToNext());
            }
        }
        Log.d(TAG, "getAll() returned: " + transactionModelArrayList);
        return transactionModelArrayList;
    }

    public static int bulkInsert(ContentResolver contentResolver, JSONArray jsonArray) {
        Log.d(TAG, "bulkInsert() called with: contentResolver = [" + contentResolver + "], jsonArray = [" + jsonArray + "]");
        int noOfRowsInserted = contentResolver.bulkInsert(CONTENT_URI, getContentValuesArray(jsonArray));
        Log.d(TAG, "bulkInsert() returned: " + noOfRowsInserted);
        return noOfRowsInserted;
    }

    public static long insert(ContentResolver contentResolver, TransactionModel transactionModel) {
        Log.d(TAG, "insert() called with: contentResolver = [" + contentResolver + "], transactionModel = [" + transactionModel + "]");
        Uri uri = contentResolver.insert(
                CONTENT_URI,
                getContentValues(transactionModel)
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
        contentValues.put(COLUMN_TRANSACTION_ID, jsonObject.optLong(KEY_TRANSACTION_ID));
        contentValues.put(COLUMN_PAYER, jsonObject.optLong(KEY_PAYER));
        contentValues.put(COLUMN_PAYEE, jsonObject.optLong(KEY_PAYEE));
        contentValues.put(COLUMN_AMOUNT, jsonObject.optDouble(KEY_AMOUNT));
        contentValues.put(COLUMN_DATE, GeneralUtils.getTimeInMillis(jsonObject.optLong(KEY_DATE)));
        contentValues.put(COLUMN_SAVE_STATUS, SAVE_STATUS_SAVED);
        return contentValues;
    }

    private static ContentValues getContentValues(TransactionModel transactionModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TRANSACTION_ID, transactionModel.getTransactionId());
        contentValues.put(COLUMN_PAYER, transactionModel.getPayer());
        contentValues.put(COLUMN_PAYEE, transactionModel.getPayee());
        contentValues.put(COLUMN_AMOUNT, transactionModel.getAmount());
        contentValues.put(COLUMN_DATE, transactionModel.getDate());
        contentValues.put(COLUMN_SAVE_STATUS, transactionModel.getSaveStatus());
        return contentValues;
    }

    private static TransactionModel cursorToTransactionModel(Cursor cursor, boolean isJoinUserDetails) {
        Log.d(TAG, "cursorToTransactionModel() called with: cursor = [" + cursor + "], isJoinUserDetails = [" + isJoinUserDetails + "]");
        TransactionModel transactionModel = new TransactionModel(
                cursor.getLong(cursor.getColumnIndex(COLUMN_TRANSACTION_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_PAYER)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_PAYEE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SAVE_STATUS))
        );
        transactionModel.setId(cursor.getLong(0));
        if (isJoinUserDetails) {
            transactionModel.setUserDetailsModel(UserDetailsModel.cursorToUserDetailsModel(cursor));
        }
        Log.d(TAG, "cursorToTransactionModel() returned: " + transactionModel);
        return transactionModel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getPayer() {
        return payer;
    }

    public void setPayer(long payer) {
        this.payer = payer;
    }

    public long getPayee() {
        return payee;
    }

    public void setPayee(long payee) {
        this.payee = payee;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(int saveStatus) {
        this.saveStatus = saveStatus;
    }

    public UserDetailsModel getUserDetailsModel() {
        return userDetailsModel;
    }

    public void setUserDetailsModel(UserDetailsModel userDetailsModel) {
        this.userDetailsModel = userDetailsModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(transactionId);
        dest.writeLong(payer);
        dest.writeLong(payee);
        dest.writeDouble(amount);
        dest.writeLong(date);
        dest.writeInt(saveStatus);
        dest.writeParcelable(userDetailsModel, flags);
    }
}
