package rkr.binatestation.pathrakkaran.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.UserDetailsTable;

class RKRsPathrakkaranSQLiteHelper extends SQLiteOpenHelper {

    // The name of our database.
    private static final String DATABASE_NAME = "pathrakkaran.db";
    private static int DB_VERSION = 1;

    RKRsPathrakkaranSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Do the creating of the databases.
        database.execSQL(CompanyMasterTable.SQL_QUERY_CREATE_TABLE);
        database.execSQL(ProductMasterTable.SQL_QUERY_CREATE_TABLE);
        database.execSQL(AgentProductListTable.SQL_QUERY_CREATE_TABLE);
        database.execSQL(UserDetailsTable.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + CompanyMasterTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductMasterTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgentProductListTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserDetailsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do the same thing as upgrading...
        onUpgrade(db, oldVersion, newVersion);
    }
}