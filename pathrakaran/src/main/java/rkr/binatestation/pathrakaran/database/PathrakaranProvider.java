package rkr.binatestation.pathrakaran.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

import rkr.binatestation.pathrakaran.database.PathrakaranContract.AgentProductListTable;
import rkr.binatestation.pathrakaran.database.PathrakaranContract.CompanyMasterTable;
import rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable;

/**
 * Created by RKR on 30/10/2016.
 * PathrakaranProvider.
 */

public class PathrakaranProvider extends ContentProvider {
    private static final String TAG = "PathrakaranProvider";
    // These codes are returned from URI_MATCHER#match when the respective Uri matches.
    private static final int COMPANY_MASTER = 1;
    private static final int PRODUCT_MASTER = 2;
    private static final int AGENT_PRODUCT_LIST = 3;
    private static final int AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER = 4;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private RKRsPathrakaranSQLiteHelper mOpenHelper;
    private ContentResolver mContentResolver;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PathrakaranContract.CONTENT_AUTHORITY;

        // For each type of URI to add, create a corresponding code.
        matcher.addURI(authority, PathrakaranContract.PATH_COMPANY_MASTER, COMPANY_MASTER);
        matcher.addURI(authority, PathrakaranContract.PATH_PRODUCT_MASTER, PRODUCT_MASTER);
        matcher.addURI(authority, PathrakaranContract.PATH_AGENT_PRODUCT_LIST, AGENT_PRODUCT_LIST);

        // join related URIs.
        matcher.addURI(authority, PathrakaranContract.PATH_AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER,
                AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context != null) {
            mContentResolver = context.getContentResolver();
            mOpenHelper = new RKRsPathrakaranSQLiteHelper(context);
            return true;
        }
        return false;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query() called with: uri = [" + uri + "], projection = [" + Arrays.toString(projection) +
                "], selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) +
                "], sortOrder = [" + sortOrder + "]");
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            case COMPANY_MASTER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CompanyMasterTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PRODUCT_MASTER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductMasterTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AGENT_PRODUCT_LIST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AgentProductListTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                String joinedTable = AgentProductListTable.TABLE_NAME + " APL JOIN " +
                        ProductMasterTable.TABLE_NAME + " PM ON PM." + ProductMasterTable.COLUMN_PRODUCT_ID +
                        " = APL." + AgentProductListTable.COLUMN_PRODUCT_ID + " JOIN " +
                        CompanyMasterTable.TABLE_NAME + " CM ON CM." + CompanyMasterTable.COLUMN_COMPANY_ID +
                        " = PM." + ProductMasterTable.COLUMN_COMPANY_ID;
                queryBuilder.setTables(joinedTable);
                retCursor = queryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(mContentResolver, uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            // The application is querying the db for its own contents.
            case COMPANY_MASTER:
                return CompanyMasterTable.CONTENT_TYPE;
            case PRODUCT_MASTER:
                return ProductMasterTable.CONTENT_TYPE;
            case AGENT_PRODUCT_LIST:
                return AgentProductListTable.CONTENT_TYPE;
            case AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER:
                return AgentProductListTable.CONTENT_TYPE;

            // We aren't sure what is being asked of us.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Uri returnUri;
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case COMPANY_MASTER: {
                long _id = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        CompanyMasterTable.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (_id > 0) {
                    returnUri = CompanyMasterTable.buildUriWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PRODUCT_MASTER: {
                long _id = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        ProductMasterTable.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (_id > 0) {
                    returnUri = ProductMasterTable.buildUriWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case AGENT_PRODUCT_LIST: {
                long _id = mOpenHelper.getWritableDatabase().insertWithOnConflict(
                        AgentProductListTable.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (_id > 0) {
                    returnUri = ProductMasterTable.buildUriWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int rowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case COMPANY_MASTER: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        CompanyMasterTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PRODUCT_MASTER: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        ProductMasterTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case AGENT_PRODUCT_LIST: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        AgentProductListTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final int rowsUpdated;

        switch (URI_MATCHER.match(uri)) {
            case COMPANY_MASTER: {
                rowsUpdated = mOpenHelper.getWritableDatabase().updateWithOnConflict(
                        CompanyMasterTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                break;
            }
            case PRODUCT_MASTER: {
                rowsUpdated = mOpenHelper.getWritableDatabase().updateWithOnConflict(
                        ProductMasterTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                break;
            }
            case AGENT_PRODUCT_LIST: {
                rowsUpdated = mOpenHelper.getWritableDatabase().updateWithOnConflict(
                        AgentProductListTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (URI_MATCHER.match(uri)) {
            case COMPANY_MASTER: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(CompanyMasterTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            case PRODUCT_MASTER: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(ProductMasterTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            case AGENT_PRODUCT_LIST: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(AgentProductListTable.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}

